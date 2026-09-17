package gt.uvg.uvgspot.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = UVGGreen,
    onPrimary = Color.White,

    background = StudyBackground,
    onBackground = StudyTextPrimary,

    surface = StudySurface,
    onSurface = StudyTextPrimary,

    secondary = StudyTextSecondary,
    onSecondary = Color.White,

    error = StudyError,
    onError = Color.White
)

@Composable
fun UVGspotTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}