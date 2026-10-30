package gt.uvg.uvgspot.data.remote

data class StudySpaceDocument(
    val name: String,
    val location: String,
    val description: String,
    val noiseLevel: String,
    val studyTypes: List<String>,
    val hasWifi: Boolean,
    val hasPowerOutlets: Boolean,
    val footTraffic: String?,
    val imageUrl: String?
)
