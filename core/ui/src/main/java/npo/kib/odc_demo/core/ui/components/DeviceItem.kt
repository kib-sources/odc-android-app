package npo.kib.odc_demo.core.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import npo.kib.odc_demo.core.design_system.components.ODCGradientBackground
import npo.kib.odc_demo.core.design_system.ui.GradientColors
import npo.kib.odc_demo.core.design_system.ui.ThemePreviews
import npo.kib.odc_demo.core.design_system.ui.theme.ODCAppTheme

@Composable
fun DeviceItem(
    modifier: Modifier = Modifier,
    id: Int? = null,
    name: String?,
    address: String?,
    onItemClick: () -> Unit
) {
    ODCGradientBackground(
        gradientColors = GradientColors.ColorSet2, shape = RoundedCornerShape(10.dp)
    ) {
        Row(modifier = modifier.requiredHeightIn(min = 70.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(5.dp)
            .clickable { onItemClick() }, verticalAlignment = CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(0.05f))
            id?.let {
                Text(
                    text = "$id",
                    modifier = Modifier
                        .weight(0.1f)
                        .align(CenterVertically),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.weight(0.05f))
            } ?: Spacer(modifier = Modifier.weight(0.15f))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Center) {
                Text(
                    text = name ?: "Unknown name",
                    fontSize = if (address != null) 16.sp else 20.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                if (address != null) Text(
                    text = "Device address: $address",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

            }
            Spacer(modifier = Modifier.weight(0.05f))
        }
    }
}


@ThemePreviews
@Composable
private fun DeviceItemPreview() {
    ODCAppTheme {
        Column {
            DeviceItem(
                id = 0,
                name = "Sample name",
                address = "00:00:00:00:00:00",
                onItemClick = {})
            DeviceItem(name = "Sample name", address = "00:00:00:00:00:00", onItemClick = {})
            DeviceItem(
                id = 0,
                name = "Sample name",
                address = null,
                onItemClick = {})
            DeviceItem(name = "Sample name", address = null, onItemClick = {})
        }
    }
}


//todo animated device item (?)
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