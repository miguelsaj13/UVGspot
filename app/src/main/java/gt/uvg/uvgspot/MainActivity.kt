package gt.uvg.uvgspot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import gt.uvg.uvgspot.navigation.AppNavigation
import gt.uvg.uvgspot.ui.theme.UVGspotTheme

class MainActivity : ComponentActivity() {
    // Versión para el pull request lista
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UVGspotTheme {
                AppNavigation()
            }
        }
    }
}