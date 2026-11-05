package gt.uvg.uvgspot.domain.recommendation

import gt.uvg.uvgspot.domain.model.Availability
import gt.uvg.uvgspot.domain.model.AvailabilityReport
import gt.uvg.uvgspot.domain.model.Recommendation
import gt.uvg.uvgspot.domain.model.StudyPreferences
import gt.uvg.uvgspot.domain.model.StudySpace

fun calculateRecommendations(
    spaces: List<StudySpace>,
    preferences: StudyPreferences,
    reports: List<AvailabilityReport>,
    nowMillis: Long
): List<Recommendation> = spaces.map { space ->
    val availability = estimateAvailability(space.id, reports, nowMillis)
    val matches = preferenceMatches(space, preferences)
    Recommendation(
        space = space,
        availability = availability,
        score = matches.sumOf { it.first } + availabilityScore(availability),
        reasons = matches.map { it.second }.ifEmpty { listOf("Otras características disponibles") }
    )
}.sortedWith(compareByDescending<Recommendation> { it.score }.thenBy { it.space.id })

private fun preferenceMatches(
    space: StudySpace,
    preferences: StudyPreferences
): List<Pair<Int, String>> = buildList {
    if (space.noiseLevel == preferences.noiseLevel) {
        add(4 to "Coincide con el ambiente que elegiste")
    }
    if (preferences.studyType in space.studyTypes) {
        add(4 to "Permite tu actividad de estudio")
    }
    if (preferences.requiresWifi && space.hasWifi) {
        add(2 to "Cuenta con el WiFi que necesitas")
    }
    if (preferences.requiresPowerOutlets && space.hasPowerOutlets) {
        add(2 to "Cuenta con los enchufes que necesitas")
    }
}

private fun availabilityScore(availability: Availability): Int = when (availability) {
    Availability.HIGH -> 3
    Availability.MEDIUM -> 2
    Availability.LOW -> 1
    Availability.UNKNOWN -> 0
}
