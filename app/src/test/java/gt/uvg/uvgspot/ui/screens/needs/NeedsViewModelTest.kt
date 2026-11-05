package gt.uvg.uvgspot.ui.screens.needs

import androidx.lifecycle.SavedStateHandle
import gt.uvg.uvgspot.domain.model.NoiseLevel
import gt.uvg.uvgspot.domain.model.StudyType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NeedsViewModelTest {
    @Test
    fun defaultsUseSingleEnvironmentAndActivityWithOptionalEssentialsOff() {
        val viewModel = NeedsViewModel(SavedStateHandle())
        val preferences = viewModel.uiState.value.preferences
        assertEquals(NoiseLevel.QUIET, preferences.noiseLevel)
        assertEquals(StudyType.INDIVIDUAL, preferences.studyType)
        assertFalse(preferences.requiresWifi)
        assertFalse(preferences.requiresPowerOutlets)
    }

    @Test
    fun environmentAndActivitySelectionsAreMutuallyExclusiveByType() {
        val viewModel = NeedsViewModel(SavedStateHandle())
        viewModel.selectModerateNoise()
        viewModel.selectGroupWork()
        assertEquals(NoiseLevel.MODERATE, viewModel.uiState.value.preferences.noiseLevel)
        assertEquals(StudyType.GROUP, viewModel.uiState.value.preferences.studyType)
        viewModel.selectQuiet()
        viewModel.selectIndividualStudy()
        assertEquals(NoiseLevel.QUIET, viewModel.uiState.value.preferences.noiseLevel)
        assertEquals(StudyType.INDIVIDUAL, viewModel.uiState.value.preferences.studyType)
    }

    @Test
    fun optionalEssentialsCanBeEnabledAndDisabledIndependently() {
        val viewModel = NeedsViewModel(SavedStateHandle())
        viewModel.toggleWifi()
        assertTrue(viewModel.uiState.value.preferences.requiresWifi)
        assertFalse(viewModel.uiState.value.preferences.requiresPowerOutlets)
        viewModel.togglePowerOutlets()
        assertTrue(viewModel.uiState.value.preferences.requiresPowerOutlets)
        viewModel.toggleWifi()
        viewModel.togglePowerOutlets()
        assertFalse(viewModel.uiState.value.preferences.requiresWifi)
        assertFalse(viewModel.uiState.value.preferences.requiresPowerOutlets)
    }

    @Test
    fun newViewModelRestoresPreferencesFromSavedValues() {
        val handle = SavedStateHandle()
        val first = NeedsViewModel(handle)
        first.selectModerateNoise()
        first.selectGroupWork()
        first.toggleWifi()
        first.togglePowerOutlets()
        val restoredHandle = SavedStateHandle(handle.keys().associateWith { key -> handle.get<Any?>(key) })
        val restored = NeedsViewModel(restoredHandle)
        assertEquals(first.uiState.value.preferences, restored.uiState.value.preferences)
    }
}
