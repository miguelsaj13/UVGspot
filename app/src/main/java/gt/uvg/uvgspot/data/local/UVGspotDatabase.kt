package gt.uvg.uvgspot.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteEntity::class], version = 1, exportSchema = true)
abstract class UVGspotDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao
}
