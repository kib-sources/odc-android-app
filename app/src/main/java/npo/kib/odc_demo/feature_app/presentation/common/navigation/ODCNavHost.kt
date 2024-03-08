package npo.kib.odc_demo.feature_app.presentation.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import npo.kib.odc_demo.feature_app.presentation.common.ODCAppState
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.history_screen.navigation.historyScreen
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.history_screen.navigation.navigateToHistoryScreen
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.navigation.HOME_GRAPH_ROUTE_PATTERN
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.navigation.homeGraph
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.settings_screen.navigation.settingsScreen

/**
 * Top-level navigation graph
 *  @see <a href="https://d.android.com/jetpack/compose/nav-adaptive">Nav-Adaptive</a>
 */

@Composable
fun ODCNavHost(appState: ODCAppState,
               modifier: Modifier = Modifier,
               startDestination: String = HOME_GRAPH_ROUTE_PATTERN
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        homeGraph(onHistoryClick = navController::navigateToHistoryScreen, nestedGraphs = {
            historyScreen()
        })
        settingsScreen(onBackClick = navController::popBackStack)
    }
}