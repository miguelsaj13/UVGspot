package gt.uvg.uvgspot.di

import android.content.Context
import gt.uvg.uvgspot.data.DataRepositories

class AppContainer(context: Context) {
    private val repositories = DataRepositories(context.applicationContext)
    val viewModelFactory = AppViewModelFactory(
        repositories.studySpaces,
        repositories.availability,
        repositories.favorites
    )
}
