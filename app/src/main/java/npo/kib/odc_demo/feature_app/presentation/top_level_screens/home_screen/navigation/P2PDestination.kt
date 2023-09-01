package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.navigation

import npo.kib.odc_demo.R
import npo.kib.odc_demo.ui.Icon
import npo.kib.odc_demo.ui.Icon.DrawableResourceIcon
import npo.kib.odc_demo.ui.ODCIcons

enum class P2PDestination(
    val icon: Icon,
    val iconTextId: Int,
    val screenTitleTextId: Int
                         ) {
    SEND(
        icon = DrawableResourceIcon(ODCIcons.HomeIcon),
        iconTextId = R.string.send,
        screenTitleTextId = R.string.send_screen_title,
        ),
    RECEIVE(
        icon = DrawableResourceIcon(ODCIcons.SettingsIcon),
        iconTextId = R.string.receive,
        screenTitleTextId = R.string.receive_screen_title
           ),
    ATM(
        icon = DrawableResourceIcon(ODCIcons.P2PATMIcon),
        iconTextId = R.string.get_banknotes_from_atm,
        screenTitleTextId = R.string.atm_screen_title
       )
}