package npo.kib.odc_demo.navigation

import npo.kib.odc_demo.R
import npo.kib.odc_demo.core.ui.icon.Icon
import npo.kib.odc_demo.core.ui.icon.ODCIcon

enum class TopLevelDestination(
    val selectedIcon: Icon,
    val unselectedIcon: Icon,
    val iconTextId: Int,
    val titleTextId: Int,
) {
    HOME(
        unselectedIcon = Icon.DrawableResourceIcon(ODCIcon.HomeIcon.resId),
        selectedIcon = Icon.DrawableResourceIcon(ODCIcon.HomeIconSelected.resId),
        iconTextId = R.string.home,
        titleTextId = R.string.app_name
    ),
    SETTINGS(
        unselectedIcon = Icon.DrawableResourceIcon(ODCIcon.SettingsIcon.resId),
        selectedIcon = Icon.DrawableResourceIcon(ODCIcon.SettingsIconSelected.resId),
        iconTextId = R.string.settings,
        titleTextId = R.string.settings
    )
}