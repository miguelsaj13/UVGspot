package gt.uvg.uvgspot.data.remote

data class AvailabilityReportDocument(
    val spaceId: String,
    val userId: String,
    val availability: String,
    val createdAtMillis: Long?
)
