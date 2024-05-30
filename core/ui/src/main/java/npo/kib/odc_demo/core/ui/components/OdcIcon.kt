package npo.kib.odc_demo.core.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import npo.kib.odc_demo.core.ui.icon.Icon
import npo.kib.odc_demo.core.ui.icon.Icon.DrawableResourceIcon
import npo.kib.odc_demo.core.ui.icon.Icon.ImageVectorIcon

@Composable
fun Icon.asOdcIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    contentDescription: String? = null
) = when (this) {
    is DrawableResourceIcon -> Icon(
        painter = painterResource(id = this.id),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint
    )

    is ImageVectorIcon -> Icon(
        imageVector = this.imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint
    )
}
