package npo.kib.odc_demo.feature_app.presentation.p2p_screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import npo.kib.odc_demo.feature_app.domain.model.user.AppUser
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.atm_screen.navigation.p2pATMRoute
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.navigation.P2PDestination
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.navigation.p2pRootRoute
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.navigation.p2pReceiveRoute
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen.navigation.p2pSendRoute

@Composable
fun rememberP2PCommonState(/*todo pass ODCAppState here and take some props and store them in P2PCommonState, like AppUser*/
                           navController: NavHostController = rememberNavController()
): P2PCommonState {
    return remember(key1 = navController) {
        P2PCommonState(navController)
    }
}

@Stable
class P2PCommonState(/*todo pass ODCAppState here and do something in the init block*/
                     val navController: NavHostController
) {
    private lateinit var currentUser: AppUser
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

    val defaultStartingRoute: String = p2pRootRoute

    //    var currentRoute : String
//        get
    fun popToRoot() {
//        val navOptions = navOptions {
//            popUpTo(p2pSelectionGraphRoutePattern){
//                inclusive = true
//
//            }
//            launchSingleTop = true
//        }
//        navController.navigateToP2PSelectionGraph(navOptions = navOptions)
        navController.popBackStack(route = p2pRootRoute, inclusive = false)
    }

}