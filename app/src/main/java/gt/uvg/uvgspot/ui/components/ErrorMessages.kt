package gt.uvg.uvgspot.ui.components

import gt.uvg.uvgspot.domain.model.DataError

fun DataError.toUserMessage(): String = when (this) {
    DataError.OFFLINE -> "No hay datos disponibles sin conexión. Conéctate y vuelve a intentar."
    DataError.PERMISSION_DENIED -> "No tienes permiso para realizar esta operación."
    DataError.AUTHENTICATION -> "No se pudo autorizar el reporte. Revisa la conexión e intenta de nuevo."
    DataError.INVALID_DATA -> "Los datos del espacio no tienen el formato esperado."
    DataError.NOT_FOUND -> "El espacio ya no está disponible."
    DataError.STORAGE -> "No se pudieron guardar o consultar tus favoritos."
    DataError.UNKNOWN -> "No se pudo completar la operación. Vuelve a intentar."
}
