package npo.kib.odc_demo.feature_app.presentation.p2p_screens.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.p2p_root_screen.P2PRootScreen
//fixme this graphRoute shouldn't be public, should it?
const val p2pSelectionGraphRoutePattern = "p2p_selection_graph"
const val p2pSelectionRoute = "selection_route"

fun NavController.navigateToP2PSelectionGraph(navOptions: NavOptions? = null) {
    this.navigate(p2pSelectionGraphRoutePattern, navOptions)
}

fun NavGraphBuilder.p2pSelectionGraph(onHistoryClick: () -> Unit,
                                      onATMButtonClick: () -> Unit,
                                      onReceiveButtonClick: () -> Unit,
                                      onSendButtonClick: () -> Unit,
                                      nestedGraphs: NavGraphBuilder.() -> Unit
) {
    navigation(
        route = p2pSelectionGraphRoutePattern,
        startDestination = p2pSelectionRoute
    ) {
        composable(route = p2pSelectionRoute) {
            P2PRootScreen(
                onHistoryClick = onHistoryClick,
                onATMButtonClick = onATMButtonClick,
                onReceiveButtonClick = onReceiveButtonClick,
                onSendButtonClick = onSendButtonClick
            )
        }
        nestedGraphs()
    }
}
