package gt.uvg.uvgspot.data.remote

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import gt.uvg.uvgspot.domain.model.DataError
import java.io.IOException

internal fun Throwable.toDataError(): DataError = when (this) {
    is FirebaseNetworkException, is IOException -> DataError.OFFLINE
    is FirebaseAuthException -> DataError.AUTHENTICATION
    is FirebaseFirestoreException -> when (code) {
        FirebaseFirestoreException.Code.PERMISSION_DENIED -> DataError.PERMISSION_DENIED
        FirebaseFirestoreException.Code.UNAUTHENTICATED -> DataError.AUTHENTICATION
        FirebaseFirestoreException.Code.UNAVAILABLE,
        FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> DataError.OFFLINE
        FirebaseFirestoreException.Code.NOT_FOUND -> DataError.NOT_FOUND
        else -> DataError.UNKNOWN
    }
    is IllegalArgumentException -> DataError.INVALID_DATA
    else -> DataError.UNKNOWN
}
