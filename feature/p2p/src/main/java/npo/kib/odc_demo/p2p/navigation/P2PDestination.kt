package npo.kib.odc_demo.p2p.navigation

import npo.kib.odc_demo.p2p.R.string
import npo.kib.odc_demo.ui.Icon
import npo.kib.odc_demo.ui.Icon.DrawableResourceIcon
import npo.kib.odc_demo.ui.ODCIcons

enum class P2PDestination(
    val icon: Icon,
    val iconTextId: Int,
    val screenTitleTextId: Int
) {
    ATM(
        icon = DrawableResourceIcon(ODCIcons.P2PATMIcon),
        iconTextId = string.get_banknotes_from_atm,
        screenTitleTextId = string.atm_screen_title
    ),
    RECEIVE(
        icon = DrawableResourceIcon(ODCIcons.SettingsIcon),
        iconTextId = string.receive,
        screenTitleTextId = string.receive_screen_title
    ),
    SEND(
        icon = DrawableResourceIcon(ODCIcons.HomeIcon),
        iconTextId = string.send,
        screenTitleTextId = string.send_screen_title,
    )
}