package gt.uvg.uvgspot.data.repository

import gt.uvg.uvgspot.data.local.FavoriteEntity
import gt.uvg.uvgspot.data.local.FavoritesDao
import gt.uvg.uvgspot.domain.model.DataError
import gt.uvg.uvgspot.domain.model.DataResult
import gt.uvg.uvgspot.domain.repository.CrashReporter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class RoomFavoritesRepositoryTest {

    @Test
    fun repositoryReportsExceptionToCrashReporter() = runBlocking {
        val expectedException = IllegalStateException("Database test error")
        val fakeDao = FakeFavoritesDao(expectedException)
        val fakeCrashReporter = FakeCrashReporter()

        val repository = RoomFavoritesRepository(
            dao = fakeDao,
            crashReporter = fakeCrashReporter
        )

        val result = repository.setFavorite(
            spaceId = "cit_level_4",
            isFavorite = true
        )

        assertTrue(result is DataResult.Failure)
        assertEquals(DataError.STORAGE, (result as DataResult.Failure).error)
        assertSame(expectedException, fakeCrashReporter.recordedException)
    }

    private class FakeFavoritesDao(
        private val exception: Exception
    ) : FavoritesDao {

        override fun observeFavoriteIds(): Flow<List<String>> {
            return flowOf(emptyList())
        }

        override suspend fun insertFavorite(favorite: FavoriteEntity) {
            throw exception
        }

        override suspend fun deleteFavorite(spaceId: String) = Unit
    }

    private class FakeCrashReporter : CrashReporter {
        var recordedException: Throwable? = null
            private set

        override fun recordNonFatal(exception: Throwable) {
            recordedException = exception
        }
    }
}