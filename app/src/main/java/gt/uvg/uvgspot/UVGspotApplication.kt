package gt.uvg.uvgspot

import android.app.Application
import gt.uvg.uvgspot.di.AppContainer

class UVGspotApplication : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
