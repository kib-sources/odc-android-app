package npo.kib.odc_demo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import npo.kib.odc_demo.ODCAppState
import npo.kib.odc_demo.history.historyScreen
import npo.kib.odc_demo.home.home_screen.navigation.HOME_GRAPH_ROUTE_PATTERN
import npo.kib.odc_demo.home.home_screen.navigation.homeGraph
import npo.kib.odc_demo.settings.navigation.settingsScreen
import npo.kib.odc_demo.wallet_details.navigation.walletDetailsScreen

/**
 * Top-level navigation graph
 *  @see <a href="https://d.android.com/jetpack/compose/nav-adaptive">Nav-Adaptive</a>
 */

@Composable
fun ODCNavHost(
    appState: ODCAppState,
    modifier: Modifier = Modifier,
    startDestination: String = npo.kib.odc_demo.home.home_screen.navigation.HOME_GRAPH_ROUTE_PATTERN
) {
    val navController = appState.navController
    NavHost(
        navController = navController, startDestination = startDestination, modifier = modifier
    ) {
        homeGraph(onHistoryClick = navController::navigateToHistoryScreen,
            onWalletDetailsClick = navController::navigateToWalletDetailsScreen,
            nestedGraphs = {
                historyScreen()
            })
        settingsScreen(onBackClick = navController::popBackStack)
        walletDetailsScreen(onBackClick = navController::popBackStack)
    }
}