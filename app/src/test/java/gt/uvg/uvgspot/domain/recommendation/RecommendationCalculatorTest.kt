package gt.uvg.uvgspot.domain.recommendation

import gt.uvg.uvgspot.domain.model.Availability
import gt.uvg.uvgspot.domain.model.AvailabilityReport
import gt.uvg.uvgspot.domain.model.NoiseLevel
import gt.uvg.uvgspot.domain.model.StudyPreferences
import gt.uvg.uvgspot.domain.model.StudySpace
import gt.uvg.uvgspot.domain.model.StudyType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecommendationCalculatorTest {
    @Test
    fun allSpacesRemainEvenWhenNoneOfTheirFeaturesMatch() {
        val spaces = listOf(space("b", NoiseLevel.LOUD), space("a", NoiseLevel.MODERATE))
            .map { it.copy(studyTypes = setOf(StudyType.GROUP)) }
        val recommendations = calculateRecommendations(spaces, StudyPreferences(), emptyList(), NOW)
        assertEquals(listOf("a", "b"), recommendations.map { it.space.id })
        assertTrue(recommendations.all { it.score == 0 })
    }

    @Test
    fun approvedWeightsAwardFifteenForAllMatchesAndHighAvailability() {
        val preferences = StudyPreferences(requiresWifi = true, requiresPowerOutlets = true)
        val recommendations = calculateRecommendations(
            listOf(space("a")), preferences, listOf(report("a", Availability.HIGH)), NOW
        )
        assertEquals(15, recommendations.single().score)
        assertEquals(4, recommendations.single().reasons.size)
    }

    @Test
    fun unrequestedWifiAndPowerOutletsDoNotChangeScore() {
        val spaces = listOf(
            space("a").copy(hasWifi = false, hasPowerOutlets = false),
            space("b")
        )
        val recommendations = calculateRecommendations(spaces, StudyPreferences(), emptyList(), NOW)
        assertEquals(listOf(8, 8), recommendations.map { it.score })
    }

    @Test
    fun requestedWifiChangesOrderingWithoutExcludingOtherSpaces() {
        val spaces = listOf(space("a").copy(hasWifi = false), space("b"))
        val recommendations = calculateRecommendations(
            spaces, StudyPreferences(requiresWifi = true), emptyList(), NOW
        )
        assertEquals(listOf("b", "a"), recommendations.map { it.space.id })
        assertEquals(listOf(10, 8), recommendations.map { it.score })
    }

    @Test
    fun requestedOutletsChangeOrderingWithoutExcludingOtherSpaces() {
        val spaces = listOf(space("a").copy(hasPowerOutlets = false), space("b"))
        val recommendations = calculateRecommendations(
            spaces, StudyPreferences(requiresPowerOutlets = true), emptyList(), NOW
        )
        assertEquals(listOf("b", "a"), recommendations.map { it.space.id })
        assertEquals(listOf(10, 8), recommendations.map { it.score })
    }

    @Test
    fun availabilityAddsThreeTwoOneOrZeroToMatchingSpaces() {
        val spaces = listOf(space("a"), space("b"), space("c"), space("d"))
        val reports = listOf(
            report("a", Availability.LOW),
            report("b", Availability.HIGH),
            report("c", Availability.MEDIUM)
        )
        val recommendations = calculateRecommendations(spaces, StudyPreferences(), reports, NOW)
        assertEquals(listOf("b", "c", "a", "d"), recommendations.map { it.space.id })
        assertEquals(listOf(11, 10, 9, 8), recommendations.map { it.score })
    }

    @Test
    fun changingEnvironmentAndActivityChangesBestMatch() {
        val spaces = listOf(
            space("quiet"),
            space("group", NoiseLevel.MODERATE).copy(studyTypes = setOf(StudyType.GROUP))
        )
        val preferences = StudyPreferences(NoiseLevel.MODERATE, StudyType.GROUP)
        val recommendations = calculateRecommendations(spaces, preferences, emptyList(), NOW)
        assertEquals("group", recommendations.first().space.id)
        assertEquals(8, recommendations.first().score)
        assertEquals(0, recommendations.last().score)
    }

    @Test
    fun stableIdBreaksEqualScoresRegardlessOfInputOrder() {
        val spaces = listOf(space("z"), space("a"))
        val first = calculateRecommendations(spaces, StudyPreferences(), emptyList(), NOW)
        val second = calculateRecommendations(spaces.reversed(), StudyPreferences(), emptyList(), NOW)
        assertEquals(listOf("a", "z"), first.map { it.space.id })
        assertEquals(first, second)
    }

    @Test
    fun expiredEvidenceStopsAddingAvailabilityPoints() {
        val spaces = listOf(space("a"))
        val reports = listOf(report("a", Availability.HIGH))
        val current = calculateRecommendations(spaces, StudyPreferences(), reports, NOW).single()
        val expired = calculateRecommendations(
            spaces, StudyPreferences(), reports, NOW + 30L * 60L * 1_000L + 1
        ).single()
        assertEquals(11, current.score)
        assertEquals(8, expired.score)
        assertEquals(Availability.UNKNOWN, expired.availability)
    }

    private fun space(id: String, noiseLevel: NoiseLevel = NoiseLevel.QUIET) = StudySpace(
        id = id,
        name = id,
        location = "Edificio de prueba",
        description = "Espacio de prueba",
        noiseLevel = noiseLevel,
        studyTypes = setOf(StudyType.INDIVIDUAL),
        hasWifi = true,
        hasPowerOutlets = true
    )

    private fun report(spaceId: String, availability: Availability) = AvailabilityReport(
        id = "report-$spaceId",
        spaceId = spaceId,
        userId = "student",
        availability = availability,
        createdAtMillis = NOW
    )

    private companion object {
        const val NOW = 10_000_000L
    }
}
