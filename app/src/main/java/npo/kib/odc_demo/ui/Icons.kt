package npo.kib.odc_demo.ui

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector
import npo.kib.odc_demo.R

object ODCIcons {
    val HomeIcon = R.drawable.home_icon
    val HomeIconSelected = R.drawable.home_icon_selected
    val SettingsIcon = R.drawable.settings_icon
    val SettingsIconSelected = R.drawable.settings_icon_selected
    val P2PSendIcon = R.drawable.send_money
    val P2PReceiveIcon = R.drawable.receive_money
    val P2PATMIcon = R.drawable.atm_top_up_icon

}


sealed class Icon {
    data class ImageVectorIcon(val imageVector: ImageVector) : Icon()
    data class DrawableResourceIcon(@DrawableRes val id: Int) : Icon()
}