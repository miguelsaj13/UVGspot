package gt.uvg.uvgspot.testutil

import gt.uvg.uvgspot.domain.model.Availability
import gt.uvg.uvgspot.domain.model.AvailabilityReport
import gt.uvg.uvgspot.domain.model.DataResult
import gt.uvg.uvgspot.domain.model.NoiseLevel
import gt.uvg.uvgspot.domain.model.ReportSubmission
import gt.uvg.uvgspot.domain.model.StudySpace
import gt.uvg.uvgspot.domain.model.StudyType
import gt.uvg.uvgspot.domain.model.SubmissionStatus
import gt.uvg.uvgspot.domain.repository.AvailabilityRepository
import gt.uvg.uvgspot.domain.repository.FavoritesRepository
import gt.uvg.uvgspot.domain.repository.StudySpaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

val TEST_SPACE = StudySpace(
    id = "cit-four",
    name = "CIT nivel 4",
    location = "CIT, nivel 4",
    description = "Espacio de prueba",
    noiseLevel = NoiseLevel.QUIET,
    studyTypes = setOf(StudyType.INDIVIDUAL),
    hasWifi = true,
    hasPowerOutlets = true
)

class FakeStudySpaceRepository : StudySpaceRepository {
    val spaces = MutableStateFlow<DataResult<List<StudySpace>>>(DataResult.Loading)
    val detail = MutableStateFlow<DataResult<StudySpace?>>(DataResult.Loading)
    var collectionObservations = 0
    var requestedId: String? = null

    override fun observeStudySpaces(): Flow<DataResult<List<StudySpace>>> {
        collectionObservations += 1
        return spaces
    }

    override fun observeStudySpace(spaceId: String): Flow<DataResult<StudySpace?>> {
        requestedId = spaceId
        return detail
    }
}

class FakeAvailabilityRepository : AvailabilityRepository {
    val reports = MutableStateFlow<DataResult<List<AvailabilityReport>>>(DataResult.Success(emptyList()))
    val submission = MutableStateFlow(ReportSubmission(SubmissionStatus.IDLE))
    var submissions = 0
    var submittedSpaceId: String? = null
    var submittedAvailability: Availability? = null

    override fun observeRecentReports(): Flow<DataResult<List<AvailabilityReport>>> = reports
    override fun observeSubmission(spaceId: String): Flow<ReportSubmission> = submission

    override suspend fun submitReport(spaceId: String, availability: Availability) {
        submissions += 1
        submittedSpaceId = spaceId
        submittedAvailability = availability
        submission.value = ReportSubmission(SubmissionStatus.PENDING, "report-one")
    }
}

class FakeFavoritesRepository : FavoritesRepository {
    val favorites = MutableStateFlow<DataResult<Set<String>>>(DataResult.Success(emptySet()))
    var writeResult: DataResult<Unit> = DataResult.Success(Unit)

    override fun observeFavoriteIds(): Flow<DataResult<Set<String>>> = favorites

    override suspend fun setFavorite(spaceId: String, isFavorite: Boolean): DataResult<Unit> {
        if (writeResult is DataResult.Success) {
            val current = (favorites.value as? DataResult.Success)?.value.orEmpty()
            favorites.value = DataResult.Success(if (isFavorite) current + spaceId else current - spaceId)
        }
        return writeResult
    }
}
