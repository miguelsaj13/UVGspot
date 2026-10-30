package gt.uvg.uvgspot.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import gt.uvg.uvgspot.data.local.UVGspotDatabase
import gt.uvg.uvgspot.data.repository.RoomFavoritesRepository
import gt.uvg.uvgspot.domain.model.DataResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesPersistenceTest {
    @Test
    fun favoriteSurvivesDatabaseCloseAndReopenAndCanBeRemoved() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val databaseName = "favorites-persistence-test.db"
        context.deleteDatabase(databaseName)
        var database = Room.databaseBuilder(context, UVGspotDatabase::class.java, databaseName).build()
        try {
            val repository = RoomFavoritesRepository(database.favoritesDao())
            assertTrue(repository.setFavorite("cit-4", true) is DataResult.Success)
            database.close()
            database = Room.databaseBuilder(context, UVGspotDatabase::class.java, databaseName).build()
            assertEquals(listOf("cit-4"), database.favoritesDao().observeFavoriteIds().first())
            val reopenedRepository = RoomFavoritesRepository(database.favoritesDao())
            assertTrue(reopenedRepository.setFavorite("cit-4", false) is DataResult.Success)
            assertTrue(database.favoritesDao().observeFavoriteIds().first().isEmpty())
        } finally {
            database.close()
            context.deleteDatabase(databaseName)
        }
    }
}
