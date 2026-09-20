package gt.uvg.uvgspot.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import gt.uvg.uvgspot.ui.theme.StudyGray50
import gt.uvg.uvgspot.ui.theme.StudyTextSecondary
import gt.uvg.uvgspot.ui.theme.UVGspotTheme


@Composable
fun LoginRoute(
    onLoginClick: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    LoginScreen(
        carnet = viewModel.carnet,
        email = viewModel.email,
        password = viewModel.password,
        carnetError = viewModel.carnetError,
        emailError = viewModel.emailError,
        passwordError = viewModel.passwordError,
        isFormValid = viewModel.isFormValid,
        onCarnetChange = viewModel::onCarnetChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = onLoginClick
    )
}

@Composable
fun LoginScreen(
    carnet: String,
    email: String,
    password: String,
    carnetError: String?,
    emailError: String?,
    passwordError: String?,
    isFormValid: Boolean,
    onCarnetChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 44.dp)
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(6.dp)
                    )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "UVGspot",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "¡Bienvenido!",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ingresa y encuentra un lugar perfecto para lo que necesites en UVG.",
            style = MaterialTheme.typography.bodyMedium,
            color = StudyTextSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        LoginTextField(
            label = "CARNET",
            value = carnet,
            onValueChange = onCarnetChange,
            errorMessage = carnetError,
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(18.dp))

        LoginTextField(
            label = "EMAIL",
            value = email,
            onValueChange = onEmailChange,
            errorMessage = emailError,
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(18.dp))

        LoginTextField(
            label = "PASSWORD",
            value = password,
            onValueChange = onPasswordChange,
            errorMessage = passwordError,
            keyboardType = KeyboardType.Password,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onLoginClick,
            enabled = isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun LoginTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    errorMessage: String?,
    keyboardType: KeyboardType,
    isPassword: Boolean = false
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = StudyTextSecondary
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = errorMessage != null,
            supportingText = {
                if (errorMessage != null) {
                    Text(text = errorMessage)
                }
            },
            shape = RoundedCornerShape(16.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
            visualTransformation = if (isPassword) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                errorContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = StudyGray50
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    UVGspotTheme {
        LoginScreen(
            carnet = "24123",
            email = "student@uvg.edu.gt",
            password = "12345678",
            carnetError = null,
            emailError = null,
            passwordError = null,
            isFormValid = true,
            onCarnetChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {}
        )
    }
}