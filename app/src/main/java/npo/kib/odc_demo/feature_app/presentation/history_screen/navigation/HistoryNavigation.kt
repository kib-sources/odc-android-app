package npo.kib.odc_demo.feature_app.presentation.history_screen.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions

const val historyRoute = "history_route"

fun NavController.navigateToHistoryScreen(navOptions: NavOptions? = null) {
    this.navigate(historyRoute, navOptions)
}

fun NavGraphBuilder.historyScreen(
) {

}