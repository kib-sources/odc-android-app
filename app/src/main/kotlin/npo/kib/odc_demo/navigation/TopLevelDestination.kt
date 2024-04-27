package npo.kib.odc_demo.navigation

import npo.kib.odc_demo.R
import npo.kib.odc_demo.ui.icon.Icon
import npo.kib.odc_demo.ui.icon.ODCIcons

enum class TopLevelDestination(
    val selectedIcon: Icon,
    val unselectedIcon: Icon,
    val iconTextId: Int,
    val titleTextId: Int,
) {
    HOME(
        unselectedIcon = Icon.DrawableResourceIcon(ODCIcons.HomeIcon),
        selectedIcon = Icon.DrawableResourceIcon(ODCIcons.HomeIconSelected),
        iconTextId = R.string.home,
        titleTextId = R.string.app_name
    ),
    SETTINGS(
        unselectedIcon = Icon.DrawableResourceIcon(ODCIcons.SettingsIcon),
        selectedIcon = Icon.DrawableResourceIcon(ODCIcons.SettingsIconSelected),
        iconTextId = R.string.settings,
        titleTextId = R.string.settings
    )
}