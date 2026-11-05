package gt.uvg.uvgspot.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import gt.uvg.uvgspot.domain.repository.AvailabilityRepository
import gt.uvg.uvgspot.domain.repository.FavoritesRepository
import gt.uvg.uvgspot.domain.repository.StudySpaceRepository
import gt.uvg.uvgspot.ui.screens.detail.SpaceDetailViewModel
import gt.uvg.uvgspot.ui.screens.login.LoginViewModel
import gt.uvg.uvgspot.ui.screens.needs.NeedsViewModel
import gt.uvg.uvgspot.ui.screens.recommendations.RecommendationsViewModel

class AppViewModelFactory(
    private val studySpaces: StudySpaceRepository,
    private val availability: AvailabilityRepository,
    private val favorites: FavoritesRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T = when (modelClass) {
        LoginViewModel::class.java -> LoginViewModel()
        NeedsViewModel::class.java -> NeedsViewModel(extras.createSavedStateHandle())
        RecommendationsViewModel::class.java -> RecommendationsViewModel(
            studySpaces, availability, favorites, extras.createSavedStateHandle()
        )
        SpaceDetailViewModel::class.java -> SpaceDetailViewModel(
            studySpaces, availability, favorites, extras.createSavedStateHandle()
        )
        else -> error("Unknown ViewModel: ${modelClass.name}")
    } as T
}
