package gt.uvg.uvgspot.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gt.uvg.uvgspot.domain.model.Availability
import gt.uvg.uvgspot.domain.model.AvailabilityReport
import gt.uvg.uvgspot.domain.model.DataError
import gt.uvg.uvgspot.domain.model.DataResult
import gt.uvg.uvgspot.domain.model.ReportSubmission
import gt.uvg.uvgspot.domain.model.StudySpace
import gt.uvg.uvgspot.domain.model.SubmissionStatus
import gt.uvg.uvgspot.domain.recommendation.estimateAvailability
import gt.uvg.uvgspot.domain.repository.AvailabilityRepository
import gt.uvg.uvgspot.domain.repository.FavoritesRepository
import gt.uvg.uvgspot.domain.repository.StudySpaceRepository
import gt.uvg.uvgspot.ui.components.toUserMessage
import kotlinx.coroutines.CancellationException
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
class SpaceDetailViewModel(
    private val studySpaceRepository: StudySpaceRepository,
    private val availabilityRepository: AvailabilityRepository,
    private val favoritesRepository: FavoritesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val spaceId = savedStateHandle.get<String>("spaceId").orEmpty()
    private val refresh = MutableStateFlow(0)
    private val clock = MutableStateFlow(System.currentTimeMillis())
    private val favoriteError = MutableStateFlow<String?>(null)
    private var savingFavorite = false
    private var submittingReport = false
    private val _uiState = MutableStateFlow(SpaceDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.subscriptionCount.map { it > 0 }.distinctUntilChanged().collectLatest { active ->
                if (active && isValidId()) coroutineScope {
                    launch { while (true) { refreshEstimates(); delay(30_000) } }
                    refresh.flatMapLatest { observeContent() }.combine(favoriteError) { state, error ->
                        state.copy(favoriteError = error ?: state.favoriteError)
                    }.collect { _uiState.value = it }
                } else if (active) {
                    _uiState.value = SpaceDetailUiState(isLoading = false, errorMessage = "El identificador del espacio no es válido.")
                }
            }
        }
    }

    private fun isValidId() = spaceId.isNotBlank() && '/' !in spaceId && spaceId.length <= 1_500

    private fun observeContent() = combine(
        studySpaceRepository.observeStudySpace(spaceId),
        availabilityRepository.observeRecentReports(),
        favoritesRepository.observeFavoriteIds(),
        availabilityRepository.observeSubmission(spaceId),
        clock
    ) { space, reports, favorites, submission, now ->
        buildState(space, reports, favorites, submission, now)
    }

    private fun buildState(
        result: DataResult<StudySpace?>,
        reports: DataResult<List<AvailabilityReport>>,
        favorites: DataResult<Set<String>>,
        submission: ReportSubmission,
        now: Long
    ): SpaceDetailUiState {
        val space = (result as? DataResult.Success)?.value
        return SpaceDetailUiState(
            isLoading = result is DataResult.Loading,
            space = space,
            availability = estimateAvailability(spaceId, (reports as? DataResult.Success)?.value.orEmpty(), now),
            isFavorite = spaceId in (favorites as? DataResult.Success)?.value.orEmpty(),
            isFromCache = (result as? DataResult.Success)?.isFromCache == true ||
                (reports as? DataResult.Success)?.isFromCache == true,
            errorMessage = when {
                result is DataResult.Failure -> result.error.toUserMessage()
                result is DataResult.Success && space == null -> "Este espacio ya no está disponible."
                else -> null
            },
            favoriteError = (favorites as? DataResult.Failure)?.error?.toUserMessage(),
            availabilityMessage = (reports as? DataResult.Failure)?.let {
                "No se pudo actualizar la estimación. ${it.error.toUserMessage()}"
            },
            submission = submission
        )
    }

    fun onFavoriteClick() {
        if (savingFavorite || !isValidId() || (_uiState.value.space == null && !_uiState.value.isFavorite)) return
        savingFavorite = true
        val target = !_uiState.value.isFavorite
        viewModelScope.launch {
            try {
                favoriteError.value = null
                val result = favoritesRepository.setFavorite(spaceId, target)
                if (result is DataResult.Failure) favoriteError.value = result.error.toUserMessage()
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                favoriteError.value = DataError.STORAGE.toUserMessage()
            } finally {
                savingFavorite = false
            }
        }
    }

    fun onReport(availability: Availability) {
        val status = _uiState.value.submission.status
        if (submittingReport || status == SubmissionStatus.SENDING || status == SubmissionStatus.PENDING ||
            availability == Availability.UNKNOWN || _uiState.value.space == null) return
        submittingReport = true
        viewModelScope.launch {
            try {
                availabilityRepository.submitReport(spaceId, availability)
            } finally {
                submittingReport = false
            }
        }
    }

    fun retry() { refresh.value += 1 }
    fun refreshEstimates() { clock.value = System.currentTimeMillis() }
}
