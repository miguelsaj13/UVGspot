package gt.uvg.uvgspot.ui.components

import gt.uvg.uvgspot.domain.model.NoiseLevel
import gt.uvg.uvgspot.domain.model.StudyType

fun NoiseLevel.toDisplayText(): String = when (this) {
    NoiseLevel.QUIET -> "Silencio"
    NoiseLevel.MODERATE -> "Ruido moderado"
    NoiseLevel.LOUD -> "Ruidoso"
}

fun StudyType.toDisplayText(): String = when (this) {
    StudyType.INDIVIDUAL -> "Individual"
    StudyType.GROUP -> "En grupo"
}

fun Set<StudyType>.toDisplayText(): String = sortedBy { it.name }
    .joinToString(" / ") { it.toDisplayText() }
