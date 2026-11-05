package gt.uvg.uvgspot.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStore
import gt.uvg.uvgspot.domain.model.Availability
import gt.uvg.uvgspot.domain.model.DataError
import gt.uvg.uvgspot.domain.model.DataResult
import gt.uvg.uvgspot.domain.model.ReportSubmission
import gt.uvg.uvgspot.domain.model.SubmissionStatus
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
class SpaceDetailViewModelTest {
    @get:Rule
    val mainDispatcher = MainDispatcherRule()
    private val store = ViewModelStore()
    private val spaces = FakeStudySpaceRepository()
    private val availability = FakeAvailabilityRepository()
    private val favorites = FakeFavoritesRepository()

    @After
    fun clearViewModels() = store.clear()

    @Test
    fun invalidArgumentProducesRecoverableErrorWithoutQueryingRepository() = runTest {
        val viewModel = observeViewModel("invalid/id")
        assertFalse(viewModel.uiState.value.isLoading)
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertEquals(null, spaces.requestedId)
    }

    @Test
    fun missingSpaceProducesExplanationRatherThanBlankContent() = runTest {
        val viewModel = observeViewModel()
        spaces.detail.value = DataResult.Success(null)
        runCurrent()
        assertFalse(viewModel.uiState.value.isLoading)
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertEquals(null, viewModel.uiState.value.space)
    }

    @Test
    fun pendingReportRemainsPendingAndRepeatedClicksDoNotSubmitAgain() = runTest {
        val viewModel = observeLoadedViewModel()
        viewModel.onReport(Availability.HIGH)
        viewModel.onReport(Availability.HIGH)
        runCurrent()
        assertEquals(1, availability.submissions)
        assertEquals(TEST_SPACE.id, availability.submittedSpaceId)
        assertEquals(SubmissionStatus.PENDING, viewModel.uiState.value.submission.status)
        viewModel.onReport(Availability.LOW)
        runCurrent()
        assertEquals(1, availability.submissions)
    }

    @Test
    fun confirmationAndRejectionFollowRepositoryEvents() = runTest {
        val viewModel = observeLoadedViewModel()
        availability.submission.value = ReportSubmission(SubmissionStatus.PENDING, "report")
        runCurrent()
        assertEquals(SubmissionStatus.PENDING, viewModel.uiState.value.submission.status)
        availability.submission.value = ReportSubmission(SubmissionStatus.CONFIRMED, "report")
        runCurrent()
        assertEquals(SubmissionStatus.CONFIRMED, viewModel.uiState.value.submission.status)
        availability.submission.value = ReportSubmission(
            SubmissionStatus.ERROR, "rejected", DataError.PERMISSION_DENIED
        )
        runCurrent()
        assertEquals(SubmissionStatus.ERROR, viewModel.uiState.value.submission.status)
        assertEquals(DataError.PERMISSION_DENIED, viewModel.uiState.value.submission.error)
    }

    @Test
    fun unknownAvailabilityCannotBeSubmitted() = runTest {
        val viewModel = observeLoadedViewModel()
        viewModel.onReport(Availability.UNKNOWN)
        runCurrent()
        assertEquals(0, availability.submissions)
    }

    @Test
    fun favoriteCanBeAddedAndRemovedThroughRepository() = runTest {
        val viewModel = observeLoadedViewModel()
        viewModel.onFavoriteClick()
        runCurrent()
        assertTrue(viewModel.uiState.value.isFavorite)
        viewModel.onFavoriteClick()
        runCurrent()
        assertFalse(viewModel.uiState.value.isFavorite)
    }

    @Test
    fun failedFavoriteWriteShowsErrorWithoutPretendingItWasSaved() = runTest {
        val viewModel = observeLoadedViewModel()
        favorites.writeResult = DataResult.Failure(DataError.STORAGE)
        viewModel.onFavoriteClick()
        runCurrent()
        assertFalse(viewModel.uiState.value.isFavorite)
        assertNotNull(viewModel.uiState.value.favoriteError)
    }

    @Test
    fun persistedFavoriteCanBeRemovedEvenWhenSpaceIsNotCached() = runTest {
        val viewModel = observeViewModel()
        spaces.detail.value = DataResult.Failure(DataError.OFFLINE)
        favorites.favorites.value = DataResult.Success(setOf(TEST_SPACE.id))
        runCurrent()
        assertTrue(viewModel.uiState.value.isFavorite)
        viewModel.onFavoriteClick()
        runCurrent()
        assertFalse(viewModel.uiState.value.isFavorite)
    }

    private fun TestScope.observeLoadedViewModel(): SpaceDetailViewModel {
        val viewModel = observeViewModel()
        spaces.detail.value = DataResult.Success(TEST_SPACE)
        runCurrent()
        return viewModel
    }

    private fun TestScope.observeViewModel(spaceId: String = TEST_SPACE.id): SpaceDetailViewModel {
        val handle = SavedStateHandle(mapOf("spaceId" to spaceId))
        val viewModel = SpaceDetailViewModel(spaces, availability, favorites, handle)
        store.put("detail", viewModel)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        runCurrent()
        return viewModel
    }
}
