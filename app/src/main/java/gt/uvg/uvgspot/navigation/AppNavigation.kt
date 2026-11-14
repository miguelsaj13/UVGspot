package gt.uvg.uvgspot.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import gt.uvg.uvgspot.domain.model.StudyPreferences
import gt.uvg.uvgspot.ui.screens.detail.SpaceDetailRoute
import gt.uvg.uvgspot.ui.screens.login.LoginRoute
import gt.uvg.uvgspot.ui.screens.needs.NeedsRoute
import gt.uvg.uvgspot.ui.screens.recommendations.ListMode
import gt.uvg.uvgspot.ui.screens.recommendations.RecommendationsRoute

object Routes {
    const val LOGIN = "login"
    const val NEEDS = "needs"
    const val RECOMMENDATIONS = "recommendations"
    const val DETAIL = "detail"

    const val NEEDS_PATTERN =
        "needs?noise={noise}&activity={activity}&wifi={wifi}&outlets={outlets}"

    const val LIST_PATTERN =
        "recommendations?noise={noise}&activity={activity}&wifi={wifi}&outlets={outlets}&mode={mode}"

    fun needs(preferences: StudyPreferences): String =
        "$NEEDS?noise=${preferences.noiseLevel.name}&activity=${preferences.studyType.name}" +
                "&wifi=${preferences.requiresWifi}&outlets=${preferences.requiresPowerOutlets}"

    fun recommendations(preferences: StudyPreferences, mode: ListMode): String =
        "$RECOMMENDATIONS?noise=${preferences.noiseLevel.name}&activity=${preferences.studyType.name}" +
                "&wifi=${preferences.requiresWifi}&outlets=${preferences.requiresPowerOutlets}&mode=${mode.name}"

    fun detail(spaceId: String): String = "$DETAIL/${Uri.encode(spaceId)}"
}

@Composable
fun AppNavigation(
    viewModelFactory: ViewModelProvider.Factory,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginRoute(
                onLoginClick = {
                    navController.navigate(Routes.NEEDS)
                },
                viewModel = viewModel(factory = viewModelFactory)
            )
        }

        composable(
            route = Routes.NEEDS_PATTERN,
            arguments = listOf(
                navArgument("noise") {
                    type = NavType.StringType
                    defaultValue = "QUIET"
                },
                navArgument("activity") {
                    type = NavType.StringType
                    defaultValue = "INDIVIDUAL"
                },
                navArgument("wifi") {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument("outlets") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) {
            val onOpenList: (StudyPreferences, ListMode) -> Unit = { preferences, mode ->
                navController.navigate(
                    Routes.recommendations(preferences, mode)
                ) {
                    launchSingleTop = true

                    if (navController.previousBackStackEntry
                            ?.destination?.route == Routes.LIST_PATTERN
                    ) {
                        popUpTo(Routes.NEEDS_PATTERN) {
                            inclusive = true
                        }
                    }
                }
            }

            NeedsRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onRecommendationsClick = {
                    onOpenList(it, ListMode.RECOMMENDATIONS)
                },
                onSpacesClick = {
                    onOpenList(it, ListMode.SPACES)
                },
                onFavoritesClick = {
                    onOpenList(it, ListMode.FAVORITES)
                },
                viewModel = viewModel(factory = viewModelFactory)
            )
        }

        composable(
            route = Routes.LIST_PATTERN,
            arguments = listOf(
                navArgument("noise") {
                    type = NavType.StringType
                    defaultValue = "QUIET"
                },
                navArgument("activity") {
                    type = NavType.StringType
                    defaultValue = "INDIVIDUAL"
                },
                navArgument("wifi") {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument("outlets") {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument("mode") {
                    type = NavType.StringType
                    defaultValue = "RECOMMENDATIONS"
                }
            )
        ) {
            val preferences = StudyPreferences(
                noiseLevel = gt.uvg.uvgspot.domain.model.NoiseLevel.entries.find {
                    it.name == navController.currentBackStackEntry
                        ?.arguments?.getString("noise")
                } ?: gt.uvg.uvgspot.domain.model.NoiseLevel.QUIET,
                studyType = gt.uvg.uvgspot.domain.model.StudyType.entries.find {
                    it.name == navController.currentBackStackEntry
                        ?.arguments?.getString("activity")
                } ?: gt.uvg.uvgspot.domain.model.StudyType.INDIVIDUAL,
                requiresWifi = navController.currentBackStackEntry
                    ?.arguments?.getBoolean("wifi") ?: false,
                requiresPowerOutlets = navController.currentBackStackEntry
                    ?.arguments?.getBoolean("outlets") ?: false
            )

            RecommendationsRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onEditClick = {
                    navController.navigate(Routes.needs(preferences)) {
                        launchSingleTop = true
                    }
                },
                onDetailsClick = {
                    navController.navigate(Routes.detail(it))
                },
                onHomeClick = {
                    navController.navigate(Routes.needs(preferences)) {
                        launchSingleTop = true
                    }
                },
                viewModel = viewModel(factory = viewModelFactory)
            )
        }

        composable(
            route = "${Routes.DETAIL}/{spaceId}",
            arguments = listOf(
                navArgument("spaceId") {
                    type = NavType.StringType
                }
            )
        ) {
            SpaceDetailRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                viewModel = viewModel(factory = viewModelFactory)
            )
        }
    }
}