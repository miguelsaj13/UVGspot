package gt.uvg.uvgspot.navigation

import androidx.lifecycle.SavedStateHandle
import gt.uvg.uvgspot.domain.model.NoiseLevel
import gt.uvg.uvgspot.domain.model.StudyPreferences
import gt.uvg.uvgspot.domain.model.StudyType

fun SavedStateHandle.readPreferences(): StudyPreferences = StudyPreferences(
    noiseLevel = NoiseLevel.entries.find { it.name == get<String>("noise") } ?: NoiseLevel.QUIET,
    studyType = StudyType.entries.find { it.name == get<String>("activity") } ?: StudyType.INDIVIDUAL,
    requiresWifi = get<Boolean>("wifi") ?: false,
    requiresPowerOutlets = get<Boolean>("outlets") ?: false
)

fun SavedStateHandle.savePreferences(preferences: StudyPreferences) {
    set("noise", preferences.noiseLevel.name)
    set("activity", preferences.studyType.name)
    set("wifi", preferences.requiresWifi)
    set("outlets", preferences.requiresPowerOutlets)
}
