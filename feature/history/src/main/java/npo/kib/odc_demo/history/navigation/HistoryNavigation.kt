package npo.kib.odc_demo.history.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import npo.kib.odc_demo.history.HistoryScreen

const val historyRoute = "history_route"

fun NavController.navigateToHistoryScreen(
    navOptions: NavOptions? = navOptions {
        launchSingleTop = true
    }
) {
    this.navigate(npo.kib.odc_demo.history.historyRoute, navOptions)
}

fun NavGraphBuilder.historyScreen(
) {
    composable(route = npo.kib.odc_demo.history.historyRoute) {
        HistoryScreen()
    }
}