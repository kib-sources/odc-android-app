package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen.SendScreen


const val sendRoute = "send_route"

fun NavController.navigateToSendScreen(navOptions: NavOptions? = null) {
    this.navigate(sendRoute, navOptions)
//    or pass options in builder:
//    this.navigate(p2pSendNavigationRoute){ /*Here is NavOptionsBuilder, like popUpTo(){}, launchSingleTop
//    = true, restoreState = true, etc */ }
}

fun NavGraphBuilder.sendScreen() {
    //todo actually still all right to use ScreenUiState here, not nested nav graphs with navigation(){}
    composable(route = sendRoute) {
        SendScreen()
    }
}