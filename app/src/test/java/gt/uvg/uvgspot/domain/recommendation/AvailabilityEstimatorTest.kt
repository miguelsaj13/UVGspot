package gt.uvg.uvgspot.domain.recommendation

import gt.uvg.uvgspot.domain.model.Availability
import gt.uvg.uvgspot.domain.model.AvailabilityReport
import org.junit.Assert.assertEquals
import org.junit.Test

class AvailabilityEstimatorTest {
    @Test
    fun noReportsMeansUnknown() {
        assertEquals(Availability.UNKNOWN, estimateAvailability(SPACE_ID, emptyList(), NOW))
    }

    @Test
    fun majorityWinsOverNewestReport() {
        val reports = listOf(
            report("a", "one", Availability.HIGH, NOW - 200),
            report("b", "two", Availability.HIGH, NOW - 100),
            report("c", "three", Availability.LOW, NOW)
        )
        assertEquals(Availability.HIGH, estimateAvailability(SPACE_ID, reports, NOW))
    }

    @Test
    fun tieUsesNewestReportAmongTiedCategories() {
        val reports = listOf(
            report("a", "one", Availability.LOW, NOW - 100),
            report("b", "two", Availability.MEDIUM, NOW)
        )
        assertEquals(Availability.MEDIUM, estimateAvailability(SPACE_ID, reports, NOW))
    }

    @Test
    fun repeatedUserContributesOnlyLatestReportForSpace() {
        val reports = listOf(
            report("a", "one", Availability.HIGH, NOW - 300),
            report("b", "one", Availability.HIGH, NOW - 200),
            report("c", "two", Availability.LOW, NOW - 100),
            report("d", "one", Availability.LOW, NOW)
        )
        assertEquals(Availability.LOW, estimateAvailability(SPACE_ID, reports, NOW))
    }

    @Test
    fun pendingNullFutureUnknownAndOtherSpaceAreNotEvidence() {
        val reports = listOf(
            report("pending", "one", Availability.HIGH, NOW, isPending = true),
            report("unresolved", "two", Availability.HIGH, null),
            report("future", "three", Availability.HIGH, NOW + 1),
            report("unknown", "four", Availability.UNKNOWN, NOW),
            report("other", "five", Availability.HIGH, NOW, spaceId = "other-space")
        )
        assertEquals(Availability.UNKNOWN, estimateAvailability(SPACE_ID, reports, NOW))
    }

    @Test
    fun validityIncludesExactThirtyMinuteBoundary() {
        val reports = listOf(report("boundary", "one", Availability.MEDIUM, NOW - WINDOW))
        assertEquals(Availability.MEDIUM, estimateAvailability(SPACE_ID, reports, NOW))
        assertEquals(Availability.UNKNOWN, estimateAvailability(SPACE_ID, reports, NOW + 1))
    }

    @Test
    fun estimationExpiresWithoutAnyNewReportEvent() {
        val reports = listOf(report("a", "one", Availability.HIGH, NOW))
        assertEquals(Availability.HIGH, estimateAvailability(SPACE_ID, reports, NOW))
        assertEquals(Availability.UNKNOWN, estimateAvailability(SPACE_ID, reports, NOW + WINDOW + 1))
    }

    @Test
    fun identicalTimestampsUseStableDocumentIdIndependentOfInputOrder() {
        val reports = listOf(
            report("a", "one", Availability.HIGH, NOW),
            report("z", "two", Availability.LOW, NOW)
        )
        assertEquals(Availability.LOW, estimateAvailability(SPACE_ID, reports, NOW))
        assertEquals(Availability.LOW, estimateAvailability(SPACE_ID, reports.reversed(), NOW))
    }

    @Test
    fun duplicateUserWithEqualTimestampUsesStableDocumentId() {
        val reports = listOf(
            report("a", "one", Availability.HIGH, NOW),
            report("z", "one", Availability.LOW, NOW)
        )
        assertEquals(Availability.LOW, estimateAvailability(SPACE_ID, reports, NOW))
        assertEquals(Availability.LOW, estimateAvailability(SPACE_ID, reports.reversed(), NOW))
    }

    @Test
    fun newerPendingReportDoesNotEraseConfirmedEvidence() {
        val reports = listOf(
            report("a", "one", Availability.MEDIUM, NOW - 100),
            report("b", "one", Availability.LOW, NOW, isPending = true)
        )
        assertEquals(Availability.MEDIUM, estimateAvailability(SPACE_ID, reports, NOW))
    }

    private fun report(
        id: String,
        userId: String,
        availability: Availability,
        createdAt: Long?,
        isPending: Boolean = false,
        spaceId: String = SPACE_ID
    ) = AvailabilityReport(id, spaceId, userId, availability, createdAt, isPending)

    private companion object {
        const val SPACE_ID = "cit-four"
        const val NOW = 10_000_000L
        const val WINDOW = 30L * 60L * 1_000L
    }
}
