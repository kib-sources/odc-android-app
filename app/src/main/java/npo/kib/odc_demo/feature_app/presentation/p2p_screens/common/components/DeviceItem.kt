package npo.kib.odc_demo.feature_app.presentation.p2p_screens.common.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.ODCGradientBackground
import npo.kib.odc_demo.ui.GradientColors
import npo.kib.odc_demo.ui.ThemePreviews
import npo.kib.odc_demo.ui.theme.ODCAppTheme

@Composable
fun DeviceItem(
    modifier: Modifier = Modifier,
    id: Int? = null,
    name: String?,
    address: String,
    onItemClick: () -> Unit
) {
    Surface(color = Color.Transparent, shape = RoundedCornerShape(10.dp)) {
    ODCGradientBackground(gradientColors = GradientColors.ColorSet2) {
        Row(modifier = modifier
//            .clip(shape = RoundedCornerShape(10.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(5.dp)
            .clickable { onItemClick() }) {
            Spacer(modifier = Modifier.weight(0.05f))
            id?.let {
                Text(
                    text = "$id",
                    modifier = Modifier
                        .weight(0.1f)
                        .align(Alignment.CenterVertically),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.weight(0.05f))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "name = ${name ?: "Unknown name"}",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "address = $address",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.weight(0.05f))
        }
    }}
}


@ThemePreviews
@Composable
private fun DeviceItemPreview() {
    ODCAppTheme {
        Column {
//            DeviceItem(id = null, name = null, address = "00:00:00:00:00:00", onItemClick = {})
            DeviceItem(id = 0,
                       name = "Sample name",
                       address = "00:00:00:00:00:00",
                       onItemClick = {})
        }
    }
}


//@Composable
//fun AnimatedDeviceItem(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
//    var visible by remember { mutableStateOf(true) }
////    val transition = updateTransition(visible, label = "itemTransition")
////
//////    val itemWidth by transition.animateDp(
//////        transitionSpec = { tween(durationMillis = 300, easing = FastOutSlowInEasing) },
//////        label = "itemWidthTransition"
//////    ) {
//////        if (it) 300.dp else 0.dp
//////    }
////
////    val itemHeight by transition.animateDp(
////        transitionSpec = { tween(durationMillis = 300, easing = FastOutSlowInEasing) },
////        label = "itemHeightTransition"
////    ) {
////        if (it) 50.dp else 0.dp
////    }
//
//    LaunchedEffect(key1 = true) {
//        for (i in 0..100) {
//            delay(1000L)
//            visible = !visible
//        }
//    }
//    AnimatedVisibility(
//        visible = visible,
//        enter =/* fadeIn() +*/ expandVertically(),
//        exit = /*fadeOut() +*/ shrinkVertically()
//    ) {
//        content()
//    }
//}
//
//
//@ThemePreviews
//@Composable
//private fun AnimatedDeviceItemPreview() {
//    ODCAppTheme {
//        AnimatedDeviceItem {
//            DeviceItemPreview()
//        }
//    }
//}