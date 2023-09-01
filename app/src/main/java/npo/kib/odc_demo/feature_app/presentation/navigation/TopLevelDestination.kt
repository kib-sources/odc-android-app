package npo.kib.odc_demo.feature_app.presentation.navigation

import npo.kib.odc_demo.R
import npo.kib.odc_demo.ui.Icon
import npo.kib.odc_demo.ui.ODCIcons

enum class TopLevelDestination(
    val selectedIcon: Icon,
    val unselectedIcon: Icon,
    val iconTextId: Int,
    val titleTextId: Int,
    val route : String
                              ) {
    HOME(
        selectedIcon = Icon.DrawableResourceIcon(ODCIcons.HomeIcon),
        unselectedIcon = Icon.DrawableResourceIcon(ODCIcons.HomeIconSelected),
        iconTextId = R.string.home,
        titleTextId = R.string.app_name,
        route = ScreenRoutes.HomeScreen.route
        ),
    SETTINGS(
        selectedIcon = Icon.DrawableResourceIcon(ODCIcons.SettingsIcon),
        unselectedIcon = Icon.DrawableResourceIcon(ODCIcons.SettingsIconSelected),
        iconTextId = R.string.settings,
        titleTextId = R.string.settings,
        route = ScreenRoutes.SettingsScreen.route
            )
}