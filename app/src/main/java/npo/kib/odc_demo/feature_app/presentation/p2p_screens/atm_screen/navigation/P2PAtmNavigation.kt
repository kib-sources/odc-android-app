package npo.kib.odc_demo.feature_app.presentation.p2p_screens.atm_screen.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.atm_screen.ATMRoute
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.atm_screen.ATMScreen
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.atm_screen.ATMViewModelNew
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveViewModelNew


const val p2pATMRoute = "p2p_atm_route"

fun NavController.navigateToATMScreen(navOptions: NavOptions? = null) {
    this.navigate(p2pATMRoute, navOptions)
}

fun NavGraphBuilder.atmScreen() {
    composable(route = p2pATMRoute) {
        val viewModel = hiltViewModel<ATMViewModelNew>(viewModelStoreOwner = it)
        ATMRoute(viewModel = viewModel)
    }
}