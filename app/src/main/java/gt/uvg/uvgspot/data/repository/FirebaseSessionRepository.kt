package gt.uvg.uvgspot.data.repository

import com.google.firebase.auth.FirebaseAuth
import gt.uvg.uvgspot.data.remote.toDataError
import gt.uvg.uvgspot.domain.model.DataError
import gt.uvg.uvgspot.domain.model.DataResult
import gt.uvg.uvgspot.domain.repository.SessionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class FirebaseSessionRepository(private val auth: FirebaseAuth) : SessionRepository {
    private val sessionMutex = Mutex()

    override suspend fun ensureAnonymousSession(): DataResult<String> = sessionMutex.withLock {
        auth.currentUser?.let { user ->
            return@withLock if (user.isAnonymous) DataResult.Success(user.uid)
            else DataResult.Failure(DataError.AUTHENTICATION)
        }
        try {
            val result = withTimeoutOrNull(AUTH_TIMEOUT_MILLIS) { auth.signInAnonymously().await() }
                ?: return@withLock DataResult.Failure(DataError.OFFLINE)
            val uid = result.user?.uid
            if (uid != null) DataResult.Success(uid) else DataResult.Failure(DataError.AUTHENTICATION)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            DataResult.Failure(exception.toDataError())
        }
    }

    private companion object {
        const val AUTH_TIMEOUT_MILLIS = 10_000L
    }
}
