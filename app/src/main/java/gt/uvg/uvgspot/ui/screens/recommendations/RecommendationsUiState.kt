package gt.uvg.uvgspot.ui.screens.recommendations

import gt.uvg.uvgspot.domain.model.Recommendation
import gt.uvg.uvgspot.domain.model.StudyPreferences

enum class ListMode { RECOMMENDATIONS, SPACES, FAVORITES }

data class RecommendationsUiState(
    val isLoading: Boolean = true,
    val recommendations: List<Recommendation> = emptyList(),
    val preferences: StudyPreferences = StudyPreferences(),
    val mode: ListMode = ListMode.RECOMMENDATIONS,
    val favoriteIds: Set<String> = emptySet(),
    val missingFavoriteIds: Set<String> = emptySet(),
    val isFromCache: Boolean = false,
    val errorMessage: String? = null,
    val availabilityMessage: String? = null,
    val favoritesError: String? = null
)
