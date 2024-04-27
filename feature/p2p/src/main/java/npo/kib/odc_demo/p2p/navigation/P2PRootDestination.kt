package npo.kib.odc_demo.p2p.navigation

import npo.kib.odc_demo.feature.p2p.R
import npo.kib.odc_demo.ui.icon.Icon
import npo.kib.odc_demo.ui.icon.Icon.DrawableResourceIcon
import npo.kib.odc_demo.ui.icon.ODCIcons

enum class P2PRootDestination(
    val icon: Icon,
    val iconTextId: Int,
    val screenTitleTextId: Int
) {
    ATM(
        icon = DrawableResourceIcon(ODCIcons.P2PATMIcon),
        iconTextId = R.string.get_banknotes_from_atm,
        screenTitleTextId = R.string.atm_screen_title
    ),
    RECEIVE(
        icon = DrawableResourceIcon(ODCIcons.SettingsIcon),
        iconTextId = R.string.receive,
        screenTitleTextId = R.string.receive_screen_title
    ),
    SEND(
        icon = DrawableResourceIcon(ODCIcons.HomeIcon),
        iconTextId = R.string.send,
        screenTitleTextId = R.string.send_screen_title,
    )
}