package npo.kib.odc_demo.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import npo.kib.odc_demo.settings.SettingsRoute

const val settingsRoute = "settings_route"

fun NavController.navigateToSettingsScreen(
    navOptions: NavOptions? = navOptions {
        launchSingleTop = true
        restoreState = true
    }
) {
    this.navigate(npo.kib.odc_demo.settings.navigation.settingsRoute, navOptions)
}

fun NavGraphBuilder.settingsScreen(onBackClick: () -> Unit) {
    composable(route = npo.kib.odc_demo.settings.navigation.settingsRoute) {
        SettingsRoute(onBackClick = onBackClick)
    }
}