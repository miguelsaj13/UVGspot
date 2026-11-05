package gt.uvg.uvgspot.domain.repository

import gt.uvg.uvgspot.domain.model.DataResult

interface SessionRepository {
    suspend fun ensureAnonymousSession(): DataResult<String>
}
