package npo.kib.odc_demo.p2p.send_screen.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import npo.kib.odc_demo.p2p.send_screen.SendRoute

const val p2pSendRoute = "p2p_send_route"

fun NavController.navigateToSendScreen(navOptions: NavOptions? = null) {
    this.navigate(
        npo.kib.odc_demo.p2p_send.navigation.p2pSendRoute,
        navOptions
    )
//    or pass options in builder:
//    this.navigate(p2pSendNavigationRoute){ /*Here is NavOptionsBuilder, like popUpTo(){}, launchSingleTop
//    = true, restoreState = true, etc */ }
}

fun NavGraphBuilder.sendScreen(navigateToP2PRoot: () -> Unit) {
    composable(route = npo.kib.odc_demo.p2p_send.navigation.p2pSendRoute) {
        SendRoute(
            navigateToP2PRoot = navigateToP2PRoot,
            navBackStackEntry = it
        )
    }
}