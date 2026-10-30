package gt.uvg.uvgspot.ui.screens.needs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import gt.uvg.uvgspot.domain.model.NoiseLevel
import gt.uvg.uvgspot.domain.model.StudyPreferences
import gt.uvg.uvgspot.domain.model.StudyType
import gt.uvg.uvgspot.navigation.readPreferences
import gt.uvg.uvgspot.navigation.savePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NeedsViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow(NeedsUiState(savedStateHandle.readPreferences()))
    val uiState = _uiState.asStateFlow()

    fun selectQuiet() = update(_uiState.value.preferences.copy(noiseLevel = NoiseLevel.QUIET))
    fun selectModerateNoise() = update(_uiState.value.preferences.copy(noiseLevel = NoiseLevel.MODERATE))
    fun selectIndividualStudy() = update(_uiState.value.preferences.copy(studyType = StudyType.INDIVIDUAL))
    fun selectGroupWork() = update(_uiState.value.preferences.copy(studyType = StudyType.GROUP))
    fun togglePowerOutlets() = update(_uiState.value.preferences.let {
        it.copy(requiresPowerOutlets = !it.requiresPowerOutlets)
    })
    fun toggleWifi() = update(_uiState.value.preferences.let { it.copy(requiresWifi = !it.requiresWifi) })

    private fun update(preferences: StudyPreferences) {
        savedStateHandle.savePreferences(preferences)
        _uiState.value = NeedsUiState(preferences)
    }
}
