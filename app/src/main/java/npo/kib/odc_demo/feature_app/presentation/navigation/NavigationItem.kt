package npo.kib.odc_demo.feature_app.presentation.navigation

import npo.kib.odc_demo.R

sealed class NavigationItem(val route : String, val icon : Int, val title : String){
    object Home : NavigationItem(route = ScreenRoutes.HomeScreen.route, icon = R.drawable.home_icon, title = "Home")
    object Settings : NavigationItem(route = ScreenRoutes.SettingsScreen.route, icon = R.drawable.settings_icon, title = "Settings")
}
