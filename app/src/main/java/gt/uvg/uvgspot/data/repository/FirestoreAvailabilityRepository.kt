package gt.uvg.uvgspot.data.repository

import android.content.SharedPreferences
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import gt.uvg.uvgspot.data.remote.mapAvailabilityReport
import gt.uvg.uvgspot.data.remote.toDataError
import gt.uvg.uvgspot.domain.model.Availability
import gt.uvg.uvgspot.domain.model.AvailabilityReport
import gt.uvg.uvgspot.domain.model.DataError
import gt.uvg.uvgspot.domain.model.DataResult
import gt.uvg.uvgspot.domain.model.ReportSubmission
import gt.uvg.uvgspot.domain.model.SubmissionStatus
import gt.uvg.uvgspot.domain.repository.AvailabilityRepository
import gt.uvg.uvgspot.domain.repository.SessionRepository
import java.util.Date
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class FirestoreAvailabilityRepository(
    private val firestore: FirebaseFirestore,
    private val sessionRepository: SessionRepository,
    private val preferences: SharedPreferences
) : AvailabilityRepository {
    private val submissions = MutableStateFlow<Map<String, ReportSubmission>>(emptyMap())
    private val submissionLocks = ConcurrentHashMap<String, Mutex>()

    override fun observeRecentReports(): Flow<DataResult<List<AvailabilityReport>>> = callbackFlow {
        trySend(DataResult.Loading)
        val received = AtomicBoolean(false)
        val timeout = launch {
            delay(INITIAL_TIMEOUT_MILLIS)
            if (!received.get()) trySend(DataResult.Failure(DataError.OFFLINE))
        }
        val cutoff = Timestamp(Date(System.currentTimeMillis() - REPORT_WINDOW_MILLIS))
        val registration = firestore.collection("availabilityReports")
            .whereGreaterThanOrEqualTo("createdAt", cutoff)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, failure ->
                received.set(true)
                if (failure != null) {
                    trySend(DataResult.Failure(failure.toDataError()))
                } else if (snapshot != null) {
                    try {
                        val reports = snapshot.documents.map { document ->
                            mapAvailabilityReport(
                                document.id, document.data.orEmpty(),
                                document.getTimestamp("createdAt")?.toDate()?.time,
                                document.metadata.hasPendingWrites()
                            )
                        }
                        trySend(if (reports.isEmpty() && snapshot.metadata.isFromCache) {
                            DataResult.Failure(DataError.OFFLINE)
                        } else {
                            DataResult.Success(reports, snapshot.metadata.isFromCache)
                        })
                    } catch (exception: RuntimeException) {
                        trySend(DataResult.Failure(DataError.INVALID_DATA))
                    }
                }
            }
        awaitClose { timeout.cancel(); registration.remove() }
    }

    override fun observeSubmission(spaceId: String): Flow<ReportSubmission> = callbackFlow {
        if (spaceId.isBlank() || '/' in spaceId) {
            trySend(ReportSubmission(SubmissionStatus.ERROR, error = DataError.INVALID_DATA))
            close()
            return@callbackFlow
        }
        if (submissions.value[spaceId] == null) {
            restoreSubmission(spaceId)?.let { updateSubmission(spaceId, it) }
        }
        val stateJob = launch {
            submissions.map { it[spaceId] ?: ReportSubmission(SubmissionStatus.IDLE) }
                .distinctUntilChanged().collect { trySend(it) }
        }
        var observedId: String? = null
        var registration: ListenerRegistration? = null
        val observerJob = launch {
            submissions.map { it[spaceId]?.reportId }.distinctUntilChanged().collect { reportId ->
                if (reportId != null && reportId != observedId) {
                    registration?.remove()
                    observedId = reportId
                    registration = observeReport(spaceId, reportId)
                }
            }
        }
        awaitClose { stateJob.cancel(); observerJob.cancel(); registration?.remove() }
    }

    override suspend fun submitReport(spaceId: String, availability: Availability) {
        val lock = submissionLocks.getOrPut(spaceId) { Mutex() }
        if (!lock.tryLock()) return
        try {
            val current = submissions.value[spaceId] ?: restoreSubmission(spaceId)?.also {
                updateSubmission(spaceId, it)
            }
            if (current?.status in setOf(SubmissionStatus.SENDING, SubmissionStatus.PENDING)) return
            if (spaceId.isBlank() || '/' in spaceId || availability == Availability.UNKNOWN) {
                updateSubmission(spaceId, ReportSubmission(SubmissionStatus.ERROR, error = DataError.INVALID_DATA))
                return
            }
            updateSubmission(spaceId, ReportSubmission(SubmissionStatus.SENDING))
            when (val session = sessionRepository.ensureAnonymousSession()) {
                is DataResult.Success -> enqueueReport(spaceId, session.value, availability)
                is DataResult.Failure -> updateSubmission(
                    spaceId, ReportSubmission(SubmissionStatus.ERROR, error = session.error)
                )
                DataResult.Loading -> updateSubmission(
                    spaceId, ReportSubmission(SubmissionStatus.ERROR, error = DataError.AUTHENTICATION)
                )
            }
        } catch (exception: CancellationException) {
            if (submissions.value[spaceId]?.status == SubmissionStatus.SENDING) {
                updateSubmission(spaceId, ReportSubmission(SubmissionStatus.IDLE))
            }
            throw exception
        } catch (exception: Exception) {
            updateSubmission(spaceId, ReportSubmission(SubmissionStatus.ERROR, error = exception.toDataError()))
        } finally {
            lock.unlock()
        }
    }

    private fun enqueueReport(spaceId: String, userId: String, availability: Availability) {
        val document = firestore.collection("availabilityReports").document()
        preferences.edit().putString(reportKey(spaceId), document.id).apply()
        val write = document.set(mapOf(
            "spaceId" to spaceId,
            "userId" to userId,
            "availability" to availability.name,
            "createdAt" to FieldValue.serverTimestamp()
        ))
        updateSubmission(spaceId, ReportSubmission(SubmissionStatus.PENDING, document.id))
        write.addOnSuccessListener {
            if (submissions.value[spaceId]?.reportId == document.id) {
                updateSubmission(spaceId, ReportSubmission(SubmissionStatus.CONFIRMED, document.id))
            }
        }.addOnFailureListener { exception ->
            if (submissions.value[spaceId]?.reportId == document.id) {
                preferences.edit().remove(reportKey(spaceId)).apply()
                updateSubmission(spaceId, ReportSubmission(SubmissionStatus.ERROR, document.id, exception.toDataError()))
            }
        }
    }

    private fun observeReport(spaceId: String, reportId: String) =
        firestore.collection("availabilityReports").document(reportId)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, failure ->
                if (submissions.value[spaceId]?.reportId != reportId) return@addSnapshotListener
                if (failure != null) {
                    val status = if (submissions.value[spaceId]?.status == SubmissionStatus.PENDING) {
                        SubmissionStatus.PENDING
                    } else SubmissionStatus.ERROR
                    updateSubmission(spaceId, ReportSubmission(status, reportId, failure.toDataError()))
                } else if (snapshot != null) {
                    try {
                        if (snapshot.exists()) {
                            val report = mapAvailabilityReport(
                                snapshot.id, snapshot.data.orEmpty(),
                                snapshot.getTimestamp("createdAt")?.toDate()?.time,
                                snapshot.metadata.hasPendingWrites()
                            )
                            require(report.spaceId == spaceId)
                            updateSubmission(spaceId, ReportSubmission(
                                if (report.isPending) SubmissionStatus.PENDING else SubmissionStatus.CONFIRMED,
                                reportId
                            ))
                        } else if (!snapshot.metadata.isFromCache) {
                            updateSubmission(spaceId, ReportSubmission(SubmissionStatus.ERROR, reportId, DataError.NOT_FOUND))
                        }
                    } catch (exception: RuntimeException) {
                        updateSubmission(spaceId, ReportSubmission(SubmissionStatus.ERROR, reportId, DataError.INVALID_DATA))
                    }
                }
            }

    private fun updateSubmission(spaceId: String, submission: ReportSubmission) {
        if (submission.reportId != null) {
            preferences.edit().putString("status.$spaceId", submission.status.name).apply()
        }
        submissions.update { it + (spaceId to submission) }
    }

    private fun restoreSubmission(spaceId: String): ReportSubmission? {
        val reportId = preferences.getString(reportKey(spaceId), null) ?: return null
        val statusName = preferences.getString("status.$spaceId", null)
        val status = SubmissionStatus.entries.firstOrNull { it.name == statusName }
            ?: SubmissionStatus.PENDING
        return ReportSubmission(status, reportId)
    }

    private fun reportKey(spaceId: String) = "report.$spaceId"

    private companion object {
        const val INITIAL_TIMEOUT_MILLIS = 10_000L
        const val REPORT_WINDOW_MILLIS = 30 * 60 * 1_000L
    }
}
