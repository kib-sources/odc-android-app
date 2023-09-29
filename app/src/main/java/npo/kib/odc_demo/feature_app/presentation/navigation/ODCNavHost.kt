package npo.kib.odc_demo.feature_app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import npo.kib.odc_demo.feature_app.presentation.common.ui.ODCAppState
import npo.kib.odc_demo.feature_app.presentation.history_screen.navigation.historyScreen
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.navigation.p2pSelectionGraph
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.navigation.HOME_GRAPH_ROUTE_PATTERN
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.navigation.homeGraph
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.navigation.homeRoute
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.settings_screen.navigation.settingsScreen

/**
 * Top-level navigation graph
 * check https://d.android.com/jetpack/compose/nav-adaptive
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
            modifier = modifier) {
        homeGraph(nestedGraphs = {
            //todo not the correct way currently, will need 2nd NavHost for P2P selection graph (...?)
            //p2p selection screen and all sub p2p screens must have a common balance block
            p2pSelectionGraph()
            historyScreen()
        })
        settingsScreen()
    }
}