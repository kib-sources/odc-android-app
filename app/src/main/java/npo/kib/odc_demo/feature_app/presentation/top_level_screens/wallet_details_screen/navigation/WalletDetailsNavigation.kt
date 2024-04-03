package npo.kib.odc_demo.feature_app.presentation.top_level_screens.wallet_details_screen.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.wallet_details_screen.WalletDetailsRoute

const val walletDetailsRoute = "wallet_details_route"

fun NavController.navigateToWalletDetailsScreen(navOptions: NavOptions? = null) {
    this.navigate(walletDetailsRoute, navOptions)
}

fun NavGraphBuilder.walletDetailsScreen(onBackClick : () -> Unit) {
    composable(route = walletDetailsRoute) {
        WalletDetailsRoute(onBackClick)
    }
}