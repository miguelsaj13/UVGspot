package gt.uvg.uvgspot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import gt.uvg.uvgspot.navigation.AppNavigation
import gt.uvg.uvgspot.ui.theme.UVGspotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
        )
        val factory = (application as UVGspotApplication).container.viewModelFactory
        setContent {
            UVGspotTheme {
                Box(Modifier.fillMaxSize().safeDrawingPadding().imePadding()) {
                    AppNavigation(viewModelFactory = factory)
                }
            }
        }
    }
}
