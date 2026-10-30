package gt.uvg.uvgspot.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import gt.uvg.uvgspot.data.remote.mapStudySpace
import gt.uvg.uvgspot.data.remote.toDataError
import gt.uvg.uvgspot.domain.model.DataError
import gt.uvg.uvgspot.domain.model.DataResult
import gt.uvg.uvgspot.domain.model.StudySpace
import gt.uvg.uvgspot.domain.repository.StudySpaceRepository
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class FirestoreStudySpaceRepository(
    private val firestore: FirebaseFirestore
) : StudySpaceRepository {
    override fun observeStudySpaces(): Flow<DataResult<List<StudySpace>>> = callbackFlow {
        trySend(DataResult.Loading)
        val received = AtomicBoolean(false)
        val timeout = launch {
            delay(INITIAL_READ_TIMEOUT_MILLIS)
            if (!received.get()) trySend(DataResult.Failure(DataError.OFFLINE))
        }
        val registration = firestore.collection("studySpaces")
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, failure ->
                received.set(true)
                if (failure != null) {
                    trySend(DataResult.Failure(failure.toDataError()))
                } else if (snapshot != null) {
                    try {
                        val spaces = snapshot.documents.map { document ->
                            mapStudySpace(document.id, document.data.orEmpty())
                        }
                        trySend(if (snapshot.metadata.isFromCache && spaces.isEmpty()) {
                            DataResult.Failure(DataError.OFFLINE)
                        } else {
                            DataResult.Success(spaces, snapshot.metadata.isFromCache)
                        })
                    } catch (exception: IllegalArgumentException) {
                        trySend(DataResult.Failure(DataError.INVALID_DATA))
                    }
                }
            }
        awaitClose { timeout.cancel(); registration.remove() }
    }

    override fun observeStudySpace(spaceId: String): Flow<DataResult<StudySpace?>> = callbackFlow {
        if (spaceId.isBlank() || '/' in spaceId) {
            trySend(DataResult.Failure(DataError.INVALID_DATA))
            close()
            return@callbackFlow
        }
        trySend(DataResult.Loading)
        val received = AtomicBoolean(false)
        val timeout = launch {
            delay(INITIAL_READ_TIMEOUT_MILLIS)
            if (!received.get()) trySend(DataResult.Failure(DataError.OFFLINE))
        }
        val registration = firestore.collection("studySpaces").document(spaceId)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, failure ->
                received.set(true)
                if (failure != null) {
                    trySend(DataResult.Failure(failure.toDataError()))
                } else if (snapshot != null) {
                    try {
                        val space = snapshot.data?.let { mapStudySpace(snapshot.id, it) }
                        trySend(if (space == null && snapshot.metadata.isFromCache) {
                            DataResult.Failure(DataError.OFFLINE)
                        } else {
                            DataResult.Success(space, snapshot.metadata.isFromCache)
                        })
                    } catch (exception: IllegalArgumentException) {
                        trySend(DataResult.Failure(DataError.INVALID_DATA))
                    }
                }
            }
        awaitClose { timeout.cancel(); registration.remove() }
    }

    private companion object {
        const val INITIAL_READ_TIMEOUT_MILLIS = 10_000L
    }
}
