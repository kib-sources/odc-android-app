package npo.kib.odc_demo.feature_app.presentation.top_level_screens.history_screen.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.history_screen.HistoryScreen

const val historyRoute = "history_route"

fun NavController.navigateToHistoryScreen(
    navOptions: NavOptions? = navOptions {
        launchSingleTop = true
    }
) {
    this.navigate(historyRoute, navOptions)
}

fun NavGraphBuilder.historyScreen(
) {
    composable(route = historyRoute) {
        HistoryScreen()
    }
}