package gt.uvg.uvgspot.data.model

data class StudySpace(
    val id: Int,
    val name: String,
    val location: String,
    val description: String,
    val availability: Availability,
    val noiseLevel: String,
    val studyType: String,
    val footTraffic: String,
    val wifi: String,
    val matchReason: String,
    val imageUrl: String
)

enum class Availability {
    HIGH,
    MEDIUM,
    LOW
}