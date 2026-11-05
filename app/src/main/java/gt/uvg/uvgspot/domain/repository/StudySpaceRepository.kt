package gt.uvg.uvgspot.domain.repository

import gt.uvg.uvgspot.domain.model.DataResult
import gt.uvg.uvgspot.domain.model.StudySpace
import kotlinx.coroutines.flow.Flow

interface StudySpaceRepository {
    fun observeStudySpaces(): Flow<DataResult<List<StudySpace>>>
    fun observeStudySpace(spaceId: String): Flow<DataResult<StudySpace?>>
}
