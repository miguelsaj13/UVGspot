package gt.uvg.uvgspot.domain.model

data class Recommendation(
    val space: StudySpace,
    val availability: Availability,
    val score: Int,
    val reasons: List<String>
)
