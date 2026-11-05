package gt.uvg.uvgspot.domain.repository

import gt.uvg.uvgspot.domain.model.Availability
import gt.uvg.uvgspot.domain.model.AvailabilityReport
import gt.uvg.uvgspot.domain.model.DataResult
import gt.uvg.uvgspot.domain.model.ReportSubmission
import kotlinx.coroutines.flow.Flow

interface AvailabilityRepository {
    fun observeRecentReports(): Flow<DataResult<List<AvailabilityReport>>>
    fun observeSubmission(spaceId: String): Flow<ReportSubmission>
    suspend fun submitReport(spaceId: String, availability: Availability)
}
