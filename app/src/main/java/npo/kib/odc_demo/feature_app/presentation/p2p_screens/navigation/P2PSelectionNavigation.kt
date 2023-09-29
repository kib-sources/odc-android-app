package npo.kib.odc_demo.feature_app.presentation.p2p_screens.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.atm_screen.navigation.atmScreen
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.navigation.receiveScreen
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen.navigation.sendScreen

private const val p2pSelectionGraphRoutePattern = "p2p_selection_graph"
const val p2pSelectionRoute = "selection_route"

fun NavController.navigateToP2PSelectionGraph(navOptions: NavOptions? = null) {
    this.navigate(p2pSelectionGraphRoutePattern, navOptions)
}

fun NavGraphBuilder.p2pSelectionGraph() {
    navigation(route = p2pSelectionGraphRoutePattern,
               startDestination = p2pSelectionRoute) {
        atmScreen()
        receiveScreen()
        sendScreen()
    }
}