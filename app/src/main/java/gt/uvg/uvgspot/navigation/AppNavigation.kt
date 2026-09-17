package gt.uvg.uvgspot.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import gt.uvg.uvgspot.ui.screens.detail.SpaceDetailRoute
import gt.uvg.uvgspot.ui.screens.login.LoginRoute
import gt.uvg.uvgspot.ui.screens.needs.NeedsRoute
import gt.uvg.uvgspot.ui.screens.recommendations.RecommendationsRoute

object Routes {
    const val LOGIN = "login"
    const val NEEDS = "needs"
    const val RECOMMENDATIONS = "recommendations"
    const val DETAIL = "detail"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        // login
        composable(Routes.LOGIN) {
            LoginRoute(
                onLoginClick = {
                    navController.navigate(Routes.NEEDS)
                }
            )
        }

        // needs
        composable(Routes.NEEDS) {
            NeedsRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onRecommendationsClick = {
                    navController.navigate(Routes.RECOMMENDATIONS)
                }
            )
        }

        // Recommendations
        composable(Routes.RECOMMENDATIONS) {
            RecommendationsRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onEditClick = {
                    navController.popBackStack()
                },
                onDetailsClick = { spaceId ->
                    navController.navigate(
                        "${Routes.DETAIL}/$spaceId"
                    )
                }
            )
        }

        // Details
        composable(
            route = "${Routes.DETAIL}/{spaceId}",
            arguments = listOf(
                navArgument("spaceId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val spaceId =
                backStackEntry.arguments?.getInt("spaceId")
                    ?: return@composable

            SpaceDetailRoute(
                spaceId = spaceId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}