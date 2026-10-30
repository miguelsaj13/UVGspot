package gt.uvg.uvgspot.data.repository

import gt.uvg.uvgspot.data.local.FavoriteEntity
import gt.uvg.uvgspot.data.local.FavoritesDao
import gt.uvg.uvgspot.domain.model.DataError
import gt.uvg.uvgspot.domain.model.DataResult
import gt.uvg.uvgspot.domain.repository.FavoritesRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class RoomFavoritesRepository(private val dao: FavoritesDao) : FavoritesRepository {
    override fun observeFavoriteIds(): Flow<DataResult<Set<String>>> = dao.observeFavoriteIds()
        .map<List<String>, DataResult<Set<String>>> { DataResult.Success(it.toSet()) }
        .onStart { emit(DataResult.Loading) }
        .catch { exception ->
            if (exception is CancellationException) throw exception
            emit(DataResult.Failure(DataError.STORAGE))
        }

    override suspend fun setFavorite(spaceId: String, isFavorite: Boolean): DataResult<Unit> {
        if (spaceId.isBlank()) return DataResult.Failure(DataError.INVALID_DATA)
        return try {
            if (isFavorite) dao.insertFavorite(FavoriteEntity(spaceId, System.currentTimeMillis()))
            else dao.deleteFavorite(spaceId)
            DataResult.Success(Unit)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            DataResult.Failure(DataError.STORAGE)
        }
    }
}
