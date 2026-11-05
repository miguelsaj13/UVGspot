package gt.uvg.uvgspot.domain.repository

import gt.uvg.uvgspot.domain.model.DataResult
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeFavoriteIds(): Flow<DataResult<Set<String>>>
    suspend fun setFavorite(spaceId: String, isFavorite: Boolean): DataResult<Unit>
}
