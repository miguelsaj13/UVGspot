package gt.uvg.uvgspot.domain.model

enum class DataError {
    OFFLINE, PERMISSION_DENIED, AUTHENTICATION, INVALID_DATA, NOT_FOUND, STORAGE, UNKNOWN
}

sealed interface DataResult<out T> {
    data object Loading : DataResult<Nothing>
    data class Success<T>(val value: T, val isFromCache: Boolean = false) : DataResult<T>
    data class Failure(val error: DataError) : DataResult<Nothing>
}
