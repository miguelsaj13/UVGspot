package gt.uvg.uvgspot.data

import gt.uvg.uvgspot.data.local.FavoriteEntity
import gt.uvg.uvgspot.data.local.FavoritesDao
import gt.uvg.uvgspot.data.repository.RoomFavoritesRepository
import gt.uvg.uvgspot.domain.model.DataError
import gt.uvg.uvgspot.domain.model.DataResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RoomFavoritesRepositoryTest {
    @Test
    fun exposesSavedIdsAndRemovalsThroughRepository() = runTest {
        val repository = RoomFavoritesRepository(FakeFavoritesDao())
        assertTrue(repository.setFavorite("cit-4", true) is DataResult.Success)
        val saved = repository.observeFavoriteIds().first { it is DataResult.Success }
        assertEquals(DataResult.Success(setOf("cit-4")), saved)
        assertTrue(repository.setFavorite("cit-4", false) is DataResult.Success)
        val removed = repository.observeFavoriteIds().first { it is DataResult.Success }
        assertEquals(DataResult.Success(emptySet<String>()), removed)
    }

    @Test
    fun invalidIdDoesNotReachDao() = runTest {
        val dao = FakeFavoritesDao()
        val result = RoomFavoritesRepository(dao).setFavorite(" ", true)
        assertEquals(DataResult.Failure(DataError.INVALID_DATA), result)
        assertEquals(0, dao.writeCount)
    }

    @Test
    fun translatesLocalWriteFailureToStorageError() = runTest {
        val repository = RoomFavoritesRepository(FakeFavoritesDao(IllegalStateException("storage failure")))
        assertEquals(DataResult.Failure(DataError.STORAGE), repository.setFavorite("cit-4", true))
    }

    @Test(expected = CancellationException::class)
    fun preservesCancellationInsteadOfTurningItIntoStorageError() = runTest {
        RoomFavoritesRepository(FakeFavoritesDao(CancellationException("cancelled")))
            .setFavorite("cit-4", true)
    }

    private class FakeFavoritesDao(private val failure: Exception? = null) : FavoritesDao {
        private val favoriteIds = MutableStateFlow<List<String>>(emptyList())
        var writeCount = 0
            private set

        override fun observeFavoriteIds(): Flow<List<String>> = favoriteIds

        override suspend fun insertFavorite(favorite: FavoriteEntity) {
            failure?.let { throw it }
            writeCount += 1
            favoriteIds.value = (favoriteIds.value + favorite.spaceId).distinct()
        }

        override suspend fun deleteFavorite(spaceId: String) {
            failure?.let { throw it }
            writeCount += 1
            favoriteIds.value = favoriteIds.value - spaceId
        }
    }
}
