package gt.uvg.uvgspot.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import gt.uvg.uvgspot.di.AppViewModelFactory
import gt.uvg.uvgspot.domain.model.Availability
import gt.uvg.uvgspot.domain.model.AvailabilityReport
import gt.uvg.uvgspot.domain.model.DataResult
import gt.uvg.uvgspot.domain.model.NoiseLevel
import gt.uvg.uvgspot.domain.model.ReportSubmission
import gt.uvg.uvgspot.domain.model.StudySpace
import gt.uvg.uvgspot.domain.model.StudyType
import gt.uvg.uvgspot.domain.model.SubmissionStatus
import gt.uvg.uvgspot.domain.repository.AvailabilityRepository
import gt.uvg.uvgspot.domain.repository.FavoritesRepository
import gt.uvg.uvgspot.domain.repository.StudySpaceRepository
import gt.uvg.uvgspot.ui.theme.UVGspotTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppNavigationTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun completeFlowCarriesStringIdAndBackPreservesPreferences() {
        val spaces = NavigationSpaces()
        val factory = AppViewModelFactory(spaces, NavigationAvailability(), NavigationFavorites())
        compose.setContent { UVGspotTheme { AppNavigation(viewModelFactory = factory) } }
        compose.onNodeWithText("Ej. 24123").performScrollTo().performTextInput("24123")
        compose.onNodeWithText("estudiante@uvg.edu.gt").performScrollTo()
            .performTextInput("student@uvg.edu.gt")
        compose.onNodeWithText("Mínimo 8 caracteres").performScrollTo().performTextInput("12345678")
        compose.onNodeWithText("Continuar").performScrollTo().performClick()
        compose.onNodeWithText("Ruido moderado").performClick()
        compose.onNodeWithText("En grupo").performClick()
        compose.onNodeWithText("Ver Recomendaciones").performClick()
        compose.onNodeWithText("Recomendaciones para ti").assertIsDisplayed()
        compose.onAllNodesWithText("Ruido moderado")[0].assertIsDisplayed()
        compose.onAllNodesWithText("En grupo")[0].assertIsDisplayed()
        compose.onNodeWithText("Ver detalles").performScrollTo().performClick()
        compose.onNodeWithText("Sobre este espacio").performScrollTo().assertIsDisplayed()
        compose.runOnIdle { assertEquals(SPACE_ID, spaces.requestedId) }
        compose.onNodeWithContentDescription("Regresar").performScrollTo().performClick()
        compose.onNodeWithText("Recomendaciones para ti").assertIsDisplayed()
        compose.onNodeWithText("Editar").performClick()
        compose.onNodeWithText("¿Qué necesitas?").assertIsDisplayed()
        compose.onNodeWithText("Ver Recomendaciones").performClick()
        compose.onAllNodesWithText("Ruido moderado")[0].assertIsDisplayed()
        compose.onAllNodesWithText("En grupo")[0].assertIsDisplayed()
    }

    private class NavigationSpaces : StudySpaceRepository {
        var requestedId: String? = null
        private val space = StudySpace(
            id = SPACE_ID,
            name = "Sala de navegación",
            location = "Edificio de pruebas, nivel 4",
            description = "Ficha para verificar el argumento de navegación.",
            noiseLevel = NoiseLevel.MODERATE,
            studyTypes = setOf(StudyType.GROUP),
            hasWifi = true,
            hasPowerOutlets = true
        )

        override fun observeStudySpaces(): Flow<DataResult<List<StudySpace>>> =
            flowOf(DataResult.Success(listOf(space)))

        override fun observeStudySpace(spaceId: String): Flow<DataResult<StudySpace?>> {
            requestedId = spaceId
            return flowOf(DataResult.Success(space.takeIf { it.id == spaceId }))
        }
    }

    private class NavigationAvailability : AvailabilityRepository {
        override fun observeRecentReports(): Flow<DataResult<List<AvailabilityReport>>> =
            flowOf(DataResult.Success(emptyList()))

        override fun observeSubmission(spaceId: String): Flow<ReportSubmission> =
            flowOf(ReportSubmission(SubmissionStatus.IDLE))

        override suspend fun submitReport(spaceId: String, availability: Availability) = Unit
    }

    private class NavigationFavorites : FavoritesRepository {
        override fun observeFavoriteIds(): Flow<DataResult<Set<String>>> =
            flowOf(DataResult.Success(emptySet()))

        override suspend fun setFavorite(spaceId: String, isFavorite: Boolean): DataResult<Unit> =
            DataResult.Success(Unit)
    }

    private companion object {
        const val SPACE_ID = "cit space-4"
    }
}
