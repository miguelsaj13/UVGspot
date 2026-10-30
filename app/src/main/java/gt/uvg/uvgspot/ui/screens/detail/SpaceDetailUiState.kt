package gt.uvg.uvgspot.ui.screens.detail

import gt.uvg.uvgspot.domain.model.Availability
import gt.uvg.uvgspot.domain.model.ReportSubmission
import gt.uvg.uvgspot.domain.model.StudySpace
import gt.uvg.uvgspot.domain.model.SubmissionStatus

data class SpaceDetailUiState(
    val isLoading: Boolean = true,
    val space: StudySpace? = null,
    val availability: Availability = Availability.UNKNOWN,
    val isFavorite: Boolean = false,
    val isFromCache: Boolean = false,
    val errorMessage: String? = null,
    val favoriteError: String? = null,
    val availabilityMessage: String? = null,
    val submission: ReportSubmission = ReportSubmission(SubmissionStatus.IDLE)
)
