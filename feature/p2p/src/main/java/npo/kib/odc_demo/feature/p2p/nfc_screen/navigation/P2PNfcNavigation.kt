package npo.kib.odc_demo.feature.p2p.nfc_screen.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import npo.kib.odc_demo.feature.p2p.nfc_screen.NfcRoute


const val p2pNfcRoute = "p2p_nfc_route"

fun NavController.navigateToNfcScreen(navOptions: NavOptions? = null) {
    this.navigate(
        p2pNfcRoute,
        navOptions
    )
}

fun NavGraphBuilder.nfcScreen(navigateToP2PRoot: () -> Unit) {
    composable(route = p2pNfcRoute) {
        NfcRoute(
            navigateToP2PRoot = navigateToP2PRoot,
            navBackStackEntry = it
        )
    }
}