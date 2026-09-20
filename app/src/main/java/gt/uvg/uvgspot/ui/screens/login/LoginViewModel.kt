package gt.uvg.uvgspot.ui.screens.login

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    var carnet by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    val carnetError: String?
        get() = when {
            carnet.isEmpty() -> null
            !carnet.all { it.isDigit() } -> "Solo se permiten números"
            carnet.length !in 5..8 -> "El carnet debe tener entre 5 y 8 dígitos"
            else -> null
        }

    val emailError: String?
        get() = when {
            email.isEmpty() -> null
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Ingresa un correo válido"
            !email.endsWith("@uvg.edu.gt") -> "Usa tu correo institucional (@uvg.edu.gt)"
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

    fun onCarnetChange(newCarnet: String) {
        carnet = newCarnet
    }
    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }
}