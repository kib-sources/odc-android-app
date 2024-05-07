package npo.kib.odc_demo.feature.wallet_details.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import npo.kib.odc_demo.feature.wallet_details.WalletDetailsRoute

const val walletDetailsRoute = "wallet_details_route"

fun NavController.navigateToWalletDetailsScreen(
    navOptions: NavOptions? = navOptions {
        launchSingleTop = true
        restoreState = true
    }
) {
    this.navigate(walletDetailsRoute, navOptions)
}

fun NavGraphBuilder.walletDetailsScreen(onBackClick: () -> Unit) {
    composable(route = walletDetailsRoute) {
        WalletDetailsRoute(onBackClick)
    }
}