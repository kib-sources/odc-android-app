package npo.kib.odc_demo.feature_app.presentation.p2p_screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.atm_screen.navigation.p2pATMRoute
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.navigation.P2PDestination
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.navigation.p2pRootRoute
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.navigation.p2pReceiveRoute
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen.navigation.p2pSendRoute

@Composable
fun rememberP2PCommonState(
    navController: NavHostController = rememberNavController()
): P2PCommonState {
    return remember(key1 = navController) {
        P2PCommonState(navController)
    }
}

@Stable
class P2PCommonState(
    val navController: NavHostController
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
                .currentBackStackEntryAsState().value?.destination

    val currentP2PDestination: P2PDestination?
        @Composable get() = when (currentDestination?.route) {
            p2pATMRoute -> P2PDestination.ATM
            p2pSendRoute -> P2PDestination.SEND
            p2pReceiveRoute -> P2PDestination.RECEIVE
            else -> null
        }

    //    var currentRoute : String
//        get
    fun popToRoot() {
        navController.popBackStack(route = p2pRootRoute, inclusive = false)
    }



}