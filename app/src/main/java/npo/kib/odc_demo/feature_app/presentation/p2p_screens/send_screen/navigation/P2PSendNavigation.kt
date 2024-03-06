package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen.SendRoute
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen.SendViewModel

const val p2pSendRoute = "p2p_send_route"

fun NavController.navigateToSendScreen(navOptions: NavOptions? = null) {
    this.navigate(
        p2pSendRoute,
        navOptions
    )
//    or pass options in builder:
//    this.navigate(p2pSendNavigationRoute){ /*Here is NavOptionsBuilder, like popUpTo(){}, launchSingleTop
//    = true, restoreState = true, etc */ }
}

fun NavGraphBuilder.sendScreen(navigateToP2PRoot: () -> Unit) {
    composable(route = p2pSendRoute) {
        val viewModel = hiltViewModel<SendViewModel>(viewModelStoreOwner = it)
        SendRoute(
            navigateToP2PRoot = navigateToP2PRoot,
            viewModel = viewModel
        )
    }
}