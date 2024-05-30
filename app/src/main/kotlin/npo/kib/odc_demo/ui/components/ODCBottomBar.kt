package npo.kib.odc_demo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.compose.ui.zIndex
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import npo.kib.odc_demo.core.design_system.ui.DevicePreviews
import npo.kib.odc_demo.core.design_system.ui.ThemePreviews
import npo.kib.odc_demo.core.design_system.ui.theme.CustomColors.Gradient_Color_1
import npo.kib.odc_demo.core.design_system.ui.theme.CustomColors.Gradient_Color_2
import npo.kib.odc_demo.core.design_system.ui.theme.ODCAppTheme
import npo.kib.odc_demo.core.ui.icon.Icon.DrawableResourceIcon
import npo.kib.odc_demo.core.ui.icon.Icon.ImageVectorIcon
import npo.kib.odc_demo.navigation.TopLevelDestination

@Composable
fun ODCBottomBar(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
    height: Dp = 60.dp,
    itemSize: Dp = height / 2,
    shape: Shape = BottomBarShape()
) {
    NavigationBar(
        modifier = modifier
            .requiredHeight(height)
            .clip(shape)
            .backgroundHorizGradient(),
        containerColor = Color.Transparent,
        windowInsets = WindowInsets(0,0,0,0)
    ) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            NavigationBarItem(modifier = Modifier
                .requiredSize(itemSize),
                selected = selected,
                alwaysShowLabel = false,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    val icon = if (selected) destination.selectedIcon
                    else destination.unselectedIcon
                    when (icon) {
                        is DrawableResourceIcon -> Icon(
                            painter = painterResource(id = icon.id),
                            contentDescription = null,
                            modifier = Modifier.requiredSize(itemSize)
                        )

                        is ImageVectorIcon -> Icon(
                            imageVector = icon.imageVector,
                            contentDescription = null,
                            modifier = Modifier.requiredSize(itemSize)
                        )
                    }
                },
                label = null)
        }
    }
}

@ThemePreviews
@DevicePreviews
@Composable
private fun ODCBottomBarPreview() {
    ODCBottomBar(
        destinations = TopLevelDestination.entries,
        onNavigateToDestination = {},
        currentDestination = null,
        modifier = Modifier.height(55.dp),
        shape = BottomBarShape()
    )
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false

fun Modifier.backgroundHorizGradient(colors: Pair<Color, Color> = Gradient_Color_1 to Gradient_Color_2) =
    background(
        brush = Brush.linearGradient(
            colors = colors.toList(), start = Offset(0f, 0f), end = Offset.Infinite
        )
    )


private class BottomBarShape : Shape {
    override fun createOutline(
        size: Size, layoutDirection: LayoutDirection, density: Density
    ): Outline {
        val h = size.height
        val w = size.width
        return Outline.Generic(Path().apply {
            arcTo(
                rect = Rect(
                    offset = Offset(0f, 0f), size = Size(2 * h, 2 * h)
                ), startAngleDegrees = 180f, sweepAngleDegrees = 90f, forceMoveTo = false
            )
            arcTo(
                rect = Rect(
                    offset = Offset(w - 2 * h, 0f), size = Size(2 * h, 2 * h)
                ), startAngleDegrees = 270f, sweepAngleDegrees = 90f, forceMoveTo = false
            )
            close()
        })
    }

}


@Preview
@Composable
private fun BottomBarShapePrev() {
    ODCAppTheme {
        Box(Modifier.requiredSize(300.dp, 80.dp)) {
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(BottomBarShape())
                    .background(Color.Green)
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
                    .zIndex(-1f)
            )
        }
    }
}





