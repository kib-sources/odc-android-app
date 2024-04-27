package npo.kib.odc_demo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import npo.kib.odc_demo.ODCAppState
import npo.kib.odc_demo.home.navigation.HOME_GRAPH_ROUTE_PATTERN
import npo.kib.odc_demo.home.navigation.homeGraph
import npo.kib.odc_demo.settings.navigation.settingsScreen

/**
 * Top-level navigation graph
 *  @see <a href="https://d.android.com/jetpack/compose/nav-adaptive">Nav-Adaptive</a>
 */

@Composable
fun ODCNavHost(
    appState: ODCAppState,
    modifier: Modifier = Modifier,
    startDestination: String = HOME_GRAPH_ROUTE_PATTERN
) {
    val navController = appState.navController
    NavHost(
        navController = navController, startDestination = startDestination, modifier = modifier
    ) {
        homeGraph(topLevelNavController = navController)
        settingsScreen(onBackClick = navController::popBackStack)
    }
}