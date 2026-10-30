package gt.uvg.uvgspot.domain.recommendation

import gt.uvg.uvgspot.domain.model.Availability
import gt.uvg.uvgspot.domain.model.AvailabilityReport

private const val REPORT_VALIDITY_MILLIS = 30L * 60L * 1_000L

fun estimateAvailability(
    spaceId: String,
    reports: List<AvailabilityReport>,
    nowMillis: Long
): Availability {
    val recentReports = reports.asSequence()
        .filter { report ->
            val createdAt = report.createdAtMillis
            report.spaceId == spaceId && !report.isPending &&
                report.availability != Availability.UNKNOWN && createdAt != null &&
                createdAt <= nowMillis && createdAt >= nowMillis - REPORT_VALIDITY_MILLIS
        }
        .sortedWith(
            compareByDescending<AvailabilityReport> { it.createdAtMillis }
                .thenByDescending { it.id }
        )
        .distinctBy { it.userId }
        .toList()
    val counts = recentReports.groupingBy { it.availability }.eachCount()
    val highestCount = counts.values.maxOrNull() ?: return Availability.UNKNOWN
    return recentReports.first { counts[it.availability] == highestCount }.availability
}
