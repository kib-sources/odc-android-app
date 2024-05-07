package npo.kib.odc_demo.feature.p2p

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import npo.kib.odc_demo.feature.atm.navigation.p2pATMRoute
import npo.kib.odc_demo.feature.p2p.navigation.P2PRootDestination
import npo.kib.odc_demo.feature.p2p.navigation.p2pRootRoute
import npo.kib.odc_demo.feature.p2p.receive_screen.navigation.p2pReceiveRoute
import npo.kib.odc_demo.feature.p2p.send_screen.navigation.p2pSendRoute

@Composable
internal fun rememberP2PCommonState(
    navController: NavHostController = rememberNavController()
): P2PCommonState {
    return remember(key1 = navController) {
        P2PCommonState(navController)
    }
}

@Stable
internal class P2PCommonState(
    val navController: NavHostController
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
                .currentBackStackEntryAsState().value?.destination

    val currentP2PRootDestination: P2PRootDestination?
        @Composable get() = when (currentDestination?.route) {
            p2pATMRoute -> P2PRootDestination.ATM
            p2pSendRoute -> P2PRootDestination.SEND
            p2pReceiveRoute -> P2PRootDestination.RECEIVE
            else -> null
        }

    fun popToRoot() {
        navController.popBackStack(route = p2pRootRoute, inclusive = false)
    }
}