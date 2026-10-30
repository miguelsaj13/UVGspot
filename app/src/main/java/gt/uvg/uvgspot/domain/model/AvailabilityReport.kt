package gt.uvg.uvgspot.domain.model

data class AvailabilityReport(
    val id: String,
    val spaceId: String,
    val userId: String,
    val availability: Availability,
    val createdAtMillis: Long?,
    val isPending: Boolean = false
)

enum class SubmissionStatus { IDLE, SENDING, PENDING, CONFIRMED, ERROR }

data class ReportSubmission(
    val status: SubmissionStatus,
    val reportId: String? = null,
    val error: DataError? = null
)
