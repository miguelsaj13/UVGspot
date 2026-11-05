package gt.uvg.uvgspot.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import gt.uvg.uvgspot.ui.screens.login.LoginRoute
import gt.uvg.uvgspot.ui.screens.login.LoginViewModel
import gt.uvg.uvgspot.ui.screens.recommendations.ListMode
import gt.uvg.uvgspot.ui.screens.recommendations.RecommendationsScreen
import gt.uvg.uvgspot.ui.screens.recommendations.RecommendationsUiState
import gt.uvg.uvgspot.ui.theme.UVGspotTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeliveryScreensTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun loginValidatesAllThreeFieldsAndClearsPasswordAfterContinue() {
        val viewModel = LoginViewModel()
        var continued = false
        compose.setContent {
            UVGspotTheme {
                LoginRoute(onLoginClick = { continued = true }, viewModel = viewModel)
            }
        }
        compose.onNodeWithText("Continuar").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("Ej. 24123").performScrollTo().performTextInput("24123")
        compose.onNodeWithText("estudiante@uvg.edu.gt").performScrollTo()
            .performTextInput("student@uvg.edu.gt")
        compose.onNodeWithText("Mínimo 8 caracteres").performScrollTo().performTextInput("12345678")
        compose.onNodeWithText("Continuar").performScrollTo().assertIsEnabled().performClick()
        compose.runOnIdle {
            assertTrue(continued)
            assertEquals("", viewModel.uiState.value.password)
        }
    }

    @Test
    fun loadingThenEmptyAreVisibleStates() {
        val state = mutableStateOf(RecommendationsUiState(mode = ListMode.SPACES))
        compose.setContent {
            UVGspotTheme {
                RecommendationsScreen(
                    uiState = state.value,
                    onBackClick = {},
                    onEditClick = {},
                    onDetailsClick = {},
                    onHomeClick = {},
                    onModeChange = {},
                    onRetry = {}
                )
            }
        }
        compose.onNodeWithText("Cargando espacios…").assertIsDisplayed()
        compose.runOnIdle { state.value = state.value.copy(isLoading = false) }
        compose.onNodeWithText("Todavía no hay espacios disponibles.").assertIsDisplayed()
    }

    @Test
    fun recoverableErrorOffersWorkingRetry() {
        var retried = false
        compose.setContent {
            UVGspotTheme {
                RecommendationsScreen(
                    uiState = RecommendationsUiState(
                        isLoading = false,
                        errorMessage = "No hay datos guardados. Conecta a internet y reintenta."
                    ),
                    onBackClick = {},
                    onEditClick = {},
                    onDetailsClick = {},
                    onHomeClick = {},
                    onModeChange = {},
                    onRetry = { retried = true }
                )
            }
        }
        compose.onNodeWithText("No hay datos guardados. Conecta a internet y reintenta.")
            .assertIsDisplayed()
        compose.onNodeWithText("Reintentar").performClick()
        compose.runOnIdle { assertTrue(retried) }
    }

    @Test
    fun localFavoriteIdRemainsVisibleWhenRemoteCardIsMissing() {
        var selectedId: String? = null
        compose.setContent {
            UVGspotTheme {
                RecommendationsScreen(
                    uiState = RecommendationsUiState(
                        isLoading = false,
                        mode = ListMode.FAVORITES,
                        favoriteIds = setOf("cit-four"),
                        missingFavoriteIds = setOf("cit-four")
                    ),
                    onBackClick = {},
                    onEditClick = {},
                    onDetailsClick = { selectedId = it },
                    onHomeClick = {},
                    onModeChange = {},
                    onRetry = {}
                )
            }
        }
        compose.onNodeWithText("Favorito guardado: cit-four", substring = true)
            .assertIsDisplayed().performClick()
        compose.runOnIdle { assertEquals("cit-four", selectedId) }
    }
}
