package npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveRoute

const val p2pReceiveRoute = "p2p_receive_route"

fun NavController.navigateToReceiveScreen(navOptions: NavOptions? = null) {
    this.navigate(
        p2pReceiveRoute,
        navOptions
    )
}

fun NavGraphBuilder.receiveScreen(navigateToP2PRoot: () -> Unit) {
    composable(route = p2pReceiveRoute) {
        ReceiveRoute(
            navigateToP2PRoot = navigateToP2PRoot,
            navBackStackEntry = it
        )
    }
}