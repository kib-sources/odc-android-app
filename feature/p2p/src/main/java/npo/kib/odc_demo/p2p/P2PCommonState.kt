package npo.kib.odc_demo.p2p

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import npo.kib.odc_demo.atm.atm_screen.navigation.p2pATMRoute
import npo.kib.odc_demo.p2p.navigation.P2PDestination
import npo.kib.odc_demo.p2p.navigation.p2pRootRoute
import npo.kib.odc_demo.p2p_send.navigation.p2pSendRoute

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
            npo.kib.odc_demo.atm.atm_screen.navigation.p2pATMRoute -> P2PDestination.ATM
            npo.kib.odc_demo.p2p_send.navigation.p2pSendRoute -> P2PDestination.SEND
            npo.kib.odc_demo.p2p.receive_screen.navigation.p2pReceiveRoute -> P2PDestination.RECEIVE
            else -> null
        }

    fun popToRoot() {
        navController.popBackStack(route = p2pRootRoute, inclusive = false)
    }
}