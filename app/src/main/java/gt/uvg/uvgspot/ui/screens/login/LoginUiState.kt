package gt.uvg.uvgspot.ui.screens.login

data class LoginUiState(
    val carnet: String = "",
    val email: String = "",
    val password: String = ""
) {
    val carnetError: String?
        get() = when {
            carnet.isEmpty() -> null
            !carnet.all { it in '0'..'9' } -> "Solo se permiten números"
            carnet.length !in 5..8 -> "El carnet debe tener entre 5 y 8 dígitos"
            else -> null
        }
    val emailError: String?
        get() = when {
            email.isEmpty() -> null
            !EMAIL_PATTERN.matches(email) -> "Ingresa un correo válido"
            !email.endsWith("@uvg.edu.gt", ignoreCase = true) -> "Usa tu correo institucional (@uvg.edu.gt)"
            else -> null
        }
    val passwordError: String?
        get() = when {
            password.isEmpty() -> null
            password.length < 8 -> "Mínimo 8 caracteres"
            else -> null
        }
    val isFormValid: Boolean
        get() = carnet.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() &&
            carnetError == null && emailError == null && passwordError == null

    private companion object {
        val EMAIL_PATTERN = Regex("[A-Za-z0-9.!#%&'+/=?^_`{|}~-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)+")
    }
}
