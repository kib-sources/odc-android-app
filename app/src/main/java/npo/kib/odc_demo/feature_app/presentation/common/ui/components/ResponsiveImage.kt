package npo.kib.odc_demo.feature_app.presentation.common.ui.components

import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout

@Composable
fun ResponsiveImage(
    painter: Painter,
    modifier: Modifier = Modifier,
    relativeSize: Float = 0.8f,
    contentDescription: String?,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    Layout(
        content = {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                modifier = modifier,
                alignment = alignment,
                contentScale = contentScale,
                alpha = alpha,
                colorFilter = colorFilter
            )
        },
        measurePolicy = { measurables, constraints ->
            val placeable = measurables.first().measure(constraints)

            // Scale the image so that it's height is 80% of parent
            val height = (constraints.maxHeight * relativeSize).toInt()
//            val width = (height * (placeable.width.toFloat() / placeable.height.toFloat())).toInt()
            val width = (placeable.width.toFloat()*relativeSize).toInt()

            // Return the new layout size
            layout(width, height) {
                placeable.placeRelative(0, 0)
            }
        }
    )
}