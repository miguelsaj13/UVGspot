package gt.uvg.uvgspot.ui.screens.recommendations

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gt.uvg.uvgspot.domain.model.AvailabilityReport
import gt.uvg.uvgspot.domain.model.DataResult
import gt.uvg.uvgspot.domain.model.StudySpace
import gt.uvg.uvgspot.domain.recommendation.calculateRecommendations
import gt.uvg.uvgspot.domain.repository.AvailabilityRepository
import gt.uvg.uvgspot.domain.repository.FavoritesRepository
import gt.uvg.uvgspot.domain.repository.StudySpaceRepository
import gt.uvg.uvgspot.navigation.readPreferences
import gt.uvg.uvgspot.ui.components.toUserMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class RecommendationsViewModel(
    private val studySpaceRepository: StudySpaceRepository,
    private val availabilityRepository: AvailabilityRepository,
    private val favoritesRepository: FavoritesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val preferences = savedStateHandle.readPreferences()
    private val mode = MutableStateFlow(
        ListMode.entries.find { it.name == savedStateHandle.get<String>("mode") }
            ?: ListMode.RECOMMENDATIONS
    )
    private val refresh = MutableStateFlow(0)
    private val clock = MutableStateFlow(System.currentTimeMillis())
    private val _uiState = MutableStateFlow(RecommendationsUiState(preferences = preferences, mode = mode.value))
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.subscriptionCount.map { it > 0 }.distinctUntilChanged().collectLatest { active ->
                if (active) coroutineScope {
                    launch { while (true) { refreshEstimates(); delay(30_000) } }
                    refresh.flatMapLatest { observeContent() }.collect { _uiState.value = it }
                }
            }
        }
    }

    private fun observeContent() = combine(
        studySpaceRepository.observeStudySpaces(),
        availabilityRepository.observeRecentReports(),
        favoritesRepository.observeFavoriteIds(),
        mode,
        clock
    ) { spaces, reports, favorites, selectedMode, now ->
        buildState(spaces, reports, favorites, selectedMode, now)
    }

    private fun buildState(
        spaces: DataResult<List<StudySpace>>,
        reports: DataResult<List<AvailabilityReport>>,
        favorites: DataResult<Set<String>>,
        selectedMode: ListMode,
        now: Long
    ): RecommendationsUiState {
        val availableSpaces = (spaces as? DataResult.Success)?.value.orEmpty()
        val favoriteIds = (favorites as? DataResult.Success)?.value.orEmpty()
        val ranked = calculateRecommendations(
            availableSpaces, preferences, (reports as? DataResult.Success)?.value.orEmpty(), now
        )
        val visible = when (selectedMode) {
            ListMode.RECOMMENDATIONS -> ranked
            ListMode.SPACES -> ranked.sortedBy { it.space.id }
            ListMode.FAVORITES -> ranked.filter { it.space.id in favoriteIds }
        }
        return RecommendationsUiState(
            isLoading = spaces is DataResult.Loading ||
                (selectedMode == ListMode.FAVORITES && favorites is DataResult.Loading),
            recommendations = visible,
            preferences = preferences,
            mode = selectedMode,
            favoriteIds = favoriteIds,
            missingFavoriteIds = if (selectedMode == ListMode.FAVORITES) {
                favoriteIds - availableSpaces.map { it.id }.toSet()
            } else emptySet(),
            isFromCache = (spaces as? DataResult.Success)?.isFromCache == true ||
                (reports as? DataResult.Success)?.isFromCache == true,
            errorMessage = (spaces as? DataResult.Failure)?.error?.toUserMessage(),
            availabilityMessage = (reports as? DataResult.Failure)?.let {
                "No se pudo actualizar la estimación. ${it.error.toUserMessage()}"
            },
            favoritesError = (favorites as? DataResult.Failure)?.error?.toUserMessage()
        )
    }

    fun onModeChange(value: ListMode) {
        savedStateHandle["mode"] = value.name
        mode.value = value
    }

    fun retry() { refresh.value += 1 }
    fun refreshEstimates() { clock.value = System.currentTimeMillis() }
}
