package npo.kib.odc_demo.home.navigation

import androidx.navigation.*
import androidx.navigation.compose.composable
import npo.kib.odc_demo.history.navigation.historyScreen
import npo.kib.odc_demo.history.navigation.navigateToHistoryScreen
import npo.kib.odc_demo.home.HomeRoute
import npo.kib.odc_demo.wallet_details.navigation.navigateToWalletDetailsScreen
import npo.kib.odc_demo.wallet_details.navigation.walletDetailsScreen


const val HOME_GRAPH_ROUTE_PATTERN = "home_graph"
const val homeRoute = "home_route"

fun NavController.navigateToHomeGraph(navOptions: NavOptions? = null) {
    this.navigate(HOME_GRAPH_ROUTE_PATTERN, navOptions)
}

fun NavGraphBuilder.homeGraph(
    topLevelNavController: NavHostController,
) {
    navigation(
        route = HOME_GRAPH_ROUTE_PATTERN, startDestination = homeRoute
    ) {
        composable(route = homeRoute) {
            HomeRoute(onHistoryClick = topLevelNavController::navigateToHistoryScreen,
                onWalletDetailsClick = topLevelNavController::navigateToWalletDetailsScreen,)
        }
        historyScreen(onBackClick = topLevelNavController::popBackStack)
        walletDetailsScreen(onBackClick = topLevelNavController::popBackStack)
    }
}