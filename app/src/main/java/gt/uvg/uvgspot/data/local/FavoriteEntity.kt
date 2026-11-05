package gt.uvg.uvgspot.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val spaceId: String,
    val savedAt: Long
)
