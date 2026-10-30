package gt.uvg.uvgspot.ui.screens.recommendations

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStore
import gt.uvg.uvgspot.domain.model.Availability
import gt.uvg.uvgspot.domain.model.DataError
import gt.uvg.uvgspot.domain.model.DataResult
import gt.uvg.uvgspot.testutil.FakeAvailabilityRepository
import gt.uvg.uvgspot.testutil.FakeFavoritesRepository
import gt.uvg.uvgspot.testutil.FakeStudySpaceRepository
import gt.uvg.uvgspot.testutil.MainDispatcherRule
import gt.uvg.uvgspot.testutil.TEST_SPACE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecommendationsViewModelTest {
    @get:Rule
    val mainDispatcher = MainDispatcherRule()
    private val store = ViewModelStore()
    private val spaces = FakeStudySpaceRepository()
    private val availability = FakeAvailabilityRepository()
    private val favorites = FakeFavoritesRepository()

    @After
    fun clearViewModels() = store.clear()

    @Test
    fun loadingStopsWhenServerReturnsEmptyCollection() = runTest {
        val viewModel = observeViewModel()
        assertTrue(viewModel.uiState.value.isLoading)
        spaces.spaces.value = DataResult.Success(emptyList())
        runCurrent()
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.recommendations.isEmpty())
        assertEquals(null, viewModel.uiState.value.errorMessage)
    }

    @Test
    fun offlineWithoutCacheBecomesErrorRatherThanEndlessLoading() = runTest {
        val viewModel = observeViewModel()
        spaces.spaces.value = DataResult.Failure(DataError.OFFLINE)
        runCurrent()
        assertFalse(viewModel.uiState.value.isLoading)
        assertNotNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun cachedSpacesRemainUsableWhenReportsCannotBeRefreshed() = runTest {
        val viewModel = observeViewModel()
        spaces.spaces.value = DataResult.Success(listOf(TEST_SPACE), isFromCache = true)
        availability.reports.value = DataResult.Failure(DataError.OFFLINE)
        runCurrent()
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isFromCache)
        assertEquals(TEST_SPACE.id, state.recommendations.single().space.id)
        assertEquals(Availability.UNKNOWN, state.recommendations.single().availability)
        assertNotNull(state.availabilityMessage)
    }

    @Test
    fun favoritesModeIncludesPersistedIdsWithoutRemoteCards() = runTest {
        val viewModel = observeViewModel()
        spaces.spaces.value = DataResult.Failure(DataError.OFFLINE)
        favorites.favorites.value = DataResult.Success(setOf("saved-offline"))
        viewModel.onModeChange(ListMode.FAVORITES)
        runCurrent()
        assertEquals(setOf("saved-offline"), viewModel.uiState.value.missingFavoriteIds)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun favoritesModeFiltersExistingCardsAndUpdatesAfterRemoval() = runTest {
        val viewModel = observeViewModel()
        spaces.spaces.value = DataResult.Success(listOf(TEST_SPACE, TEST_SPACE.copy(id = "other")))
        favorites.favorites.value = DataResult.Success(setOf(TEST_SPACE.id))
        viewModel.onModeChange(ListMode.FAVORITES)
        runCurrent()
        assertEquals(listOf(TEST_SPACE.id), viewModel.uiState.value.recommendations.map { it.space.id })
        favorites.favorites.value = DataResult.Success(emptySet())
        runCurrent()
        assertTrue(viewModel.uiState.value.recommendations.isEmpty())
    }

    @Test
    fun retryResubscribesToRepositoryAndRecovers() = runTest {
        val viewModel = observeViewModel()
        spaces.spaces.value = DataResult.Failure(DataError.PERMISSION_DENIED)
        runCurrent()
        val previousObservations = spaces.collectionObservations
        viewModel.retry()
        runCurrent()
        assertTrue(spaces.collectionObservations > previousObservations)
        spaces.spaces.value = DataResult.Success(listOf(TEST_SPACE))
        runCurrent()
        assertEquals(null, viewModel.uiState.value.errorMessage)
        assertEquals(1, viewModel.uiState.value.recommendations.size)
    }

    private fun TestScope.observeViewModel(): RecommendationsViewModel {
        val viewModel = RecommendationsViewModel(spaces, availability, favorites, SavedStateHandle())
        store.put("recommendations", viewModel)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        runCurrent()
        return viewModel
    }
}
