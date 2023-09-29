package npo.kib.odc_demo.feature_app.presentation.p2p_screens.atm_screen.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.atm_screen.ATMScreen


const val atmRoute = "atm_route"

fun NavController.navigateToATMScreen(navOptions: NavOptions? = null) {
    this.navigate(atmRoute, navOptions)
}

fun NavGraphBuilder.atmScreen() {
    composable(route = atmRoute) {
        ATMScreen()
    }
}