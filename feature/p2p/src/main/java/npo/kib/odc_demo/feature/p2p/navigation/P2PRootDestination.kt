package npo.kib.odc_demo.feature.p2p.navigation

import npo.kib.odc_demo.feature.p2p.R
import npo.kib.odc_demo.core.ui.icon.Icon
import npo.kib.odc_demo.core.ui.icon.Icon.DrawableResourceIcon
import npo.kib.odc_demo.core.ui.icon.ODCIcon

internal enum class P2PRootDestination(
    val icon: Icon,
    val iconTextId: Int,
    val screenTitleTextId: Int
) {
    ATM(
        icon = DrawableResourceIcon(ODCIcon.P2PATMIcon.resId),
        iconTextId = R.string.get_banknotes_from_atm,
        screenTitleTextId = R.string.atm_screen_title
    ),
    RECEIVE(
        icon = DrawableResourceIcon(ODCIcon.P2PReceiveIcon.resId),
        iconTextId = R.string.receive,
        screenTitleTextId = R.string.receive_screen_title
    ),
    SEND(
        icon = DrawableResourceIcon(ODCIcon.P2PSendIcon.resId),
        iconTextId = R.string.send,
        screenTitleTextId = R.string.send_screen_title,
    )
}