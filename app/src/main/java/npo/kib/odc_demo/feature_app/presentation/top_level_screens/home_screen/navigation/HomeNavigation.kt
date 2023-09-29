package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.HomeScreen


const val HOME_GRAPH_ROUTE_PATTERN = "home_graph"
const val homeRoute = "home_route"

fun NavController.navigateToHomeGraph(navOptions: NavOptions? = null) {
    this.navigate(HOME_GRAPH_ROUTE_PATTERN, navOptions)
}

fun NavGraphBuilder.homeGraph(
    nestedGraphs: NavGraphBuilder.() -> Unit
) {
    navigation(
            route = HOME_GRAPH_ROUTE_PATTERN,
            startDestination = homeRoute
    ) {
        composable(route = homeRoute){
            HomeScreen()
        }
        nestedGraphs()
    }
}