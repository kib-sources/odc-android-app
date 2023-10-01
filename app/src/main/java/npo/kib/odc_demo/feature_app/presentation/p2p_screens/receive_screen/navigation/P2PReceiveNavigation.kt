package npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveRoute


const val receiveRoute = "receive_route"

fun NavController.navigateToReceiveScreen(navOptions: NavOptions? = null) {
    this.navigate(receiveRoute, navOptions)
}

fun NavGraphBuilder.receiveScreen(/*todo pass onNavigateBack, use a "back" arrow on every p2p screen?*/) {
    composable(route = receiveRoute) {
        ReceiveRoute()
    }
}