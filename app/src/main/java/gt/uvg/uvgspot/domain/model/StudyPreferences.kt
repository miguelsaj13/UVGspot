package gt.uvg.uvgspot.domain.model

data class StudyPreferences(
    val noiseLevel: NoiseLevel = NoiseLevel.QUIET,
    val studyType: StudyType = StudyType.INDIVIDUAL,
    val requiresWifi: Boolean = false,
    val requiresPowerOutlets: Boolean = false
)
