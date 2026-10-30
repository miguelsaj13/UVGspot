package gt.uvg.uvgspot.domain.model

enum class NoiseLevel { QUIET, MODERATE, LOUD }
enum class StudyType { INDIVIDUAL, GROUP }
enum class Availability { HIGH, MEDIUM, LOW, UNKNOWN }

data class StudySpace(
    val id: String,
    val name: String,
    val location: String,
    val description: String,
    val noiseLevel: NoiseLevel,
    val studyTypes: Set<StudyType>,
    val hasWifi: Boolean,
    val hasPowerOutlets: Boolean,
    val footTraffic: String = "Sin información",
    val imageUrl: String? = null
)
