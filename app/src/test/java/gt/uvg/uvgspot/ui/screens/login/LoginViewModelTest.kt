package gt.uvg.uvgspot.ui.screens.login

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginViewModelTest {
    @Test
    fun initialFormIsDisabled() {
        val viewModel = LoginViewModel()
        assertFalse(viewModel.uiState.value.isFormValid)
    }

    @Test
    fun validThreeFieldsEnableForm() {
        val viewModel = validForm()
        val state = viewModel.uiState.value
        assertTrue(state.isFormValid)
        assertNull(state.carnetError)
        assertNull(state.emailError)
        assertNull(state.passwordError)
    }

    @Test
    fun invalidCarnetImmediatelyDisablesPreviouslyValidForm() {
        val viewModel = validForm()
        viewModel.onCarnetChange("12a45")
        assertFalse(viewModel.uiState.value.isFormValid)
        assertNotNull(viewModel.uiState.value.carnetError)
        viewModel.onCarnetChange("1234")
        assertNotNull(viewModel.uiState.value.carnetError)
        viewModel.onCarnetChange("123456789")
        assertNotNull(viewModel.uiState.value.carnetError)
    }

    @Test
    fun nonInstitutionalAndMalformedAddressesShowFieldError() {
        val viewModel = validForm()
        viewModel.onEmailChange("student@example.com")
        assertFalse(viewModel.uiState.value.isFormValid)
        assertNotNull(viewModel.uiState.value.emailError)
        viewModel.onEmailChange("not-an-address")
        assertNotNull(viewModel.uiState.value.emailError)
    }

    @Test
    fun shortPasswordShowsFieldErrorUntilCorrected() {
        val viewModel = validForm()
        viewModel.onPasswordChange("1234567")
        assertFalse(viewModel.uiState.value.isFormValid)
        assertNotNull(viewModel.uiState.value.passwordError)
        viewModel.onPasswordChange("12345678")
        assertTrue(viewModel.uiState.value.isFormValid)
        assertNull(viewModel.uiState.value.passwordError)
    }

    @Test
    fun clearingAnyRequiredFieldDisablesSubmit() {
        val viewModel = validForm()
        viewModel.onEmailChange("")
        assertFalse(viewModel.uiState.value.isFormValid)
    }

    @Test
    fun clearPasswordRemovesSecretAndNewViewModelDoesNotRestoreCredentials() {
        val viewModel = validForm()
        viewModel.clearPassword()
        assertEquals("", viewModel.uiState.value.password)
        assertFalse(viewModel.uiState.value.isFormValid)
        assertEquals("", LoginViewModel().uiState.value.password)
        assertEquals("", LoginViewModel().uiState.value.email)
    }

    private fun validForm() = LoginViewModel().apply {
        onCarnetChange("24123")
        onEmailChange("student@uvg.edu.gt")
        onPasswordChange("12345678")
    }
}
