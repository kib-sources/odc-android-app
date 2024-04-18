package npo.kib.odc_demo.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import npo.kib.odc_demo.home.HomeRoute


const val HOME_GRAPH_ROUTE_PATTERN = "home_graph"
const val homeRoute = "home_route"

fun NavController.navigateToHomeGraph(navOptions: NavOptions? = null) {
    this.navigate(npo.kib.odc_demo.home.home_screen.navigation.HOME_GRAPH_ROUTE_PATTERN, navOptions)
}

fun NavGraphBuilder.homeGraph(onWalletDetailsClick: () -> Unit, onHistoryClick: () -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit
) {
    navigation(
            route = npo.kib.odc_demo.home.home_screen.navigation.HOME_GRAPH_ROUTE_PATTERN,
            startDestination = npo.kib.odc_demo.home.home_screen.navigation.homeRoute
    ) {
        composable(route = npo.kib.odc_demo.home.home_screen.navigation.homeRoute){
            HomeRoute(onHistoryClick = onHistoryClick, onWalletDetailsClick = onWalletDetailsClick)
        }
        nestedGraphs()
    }
}