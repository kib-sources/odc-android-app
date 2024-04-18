package npo.kib.odc_demo.atm.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import npo.kib.odc_demo.atm.ATMRoute


const val p2pATMRoute = "p2p_atm_route"

fun NavController.navigateToATMScreen(navOptions: NavOptions? = null) {
    this.navigate(npo.kib.odc_demo.atm.atm_screen.navigation.p2pATMRoute, navOptions)
}

fun NavGraphBuilder.atmScreen(navigateToP2PRoot: () -> Unit) {
    composable(route = npo.kib.odc_demo.atm.atm_screen.navigation.p2pATMRoute) {
        ATMRoute(navigateToP2PRoot = navigateToP2PRoot)
    }
}