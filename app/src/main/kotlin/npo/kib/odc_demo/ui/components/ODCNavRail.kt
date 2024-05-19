package npo.kib.odc_demo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import npo.kib.odc_demo.core.design_system.ui.GradientColors
import npo.kib.odc_demo.core.design_system.ui.asList
import npo.kib.odc_demo.core.ui.icon.Icon.DrawableResourceIcon
import npo.kib.odc_demo.core.ui.icon.Icon.ImageVectorIcon
import npo.kib.odc_demo.navigation.TopLevelDestination

@Composable
fun ODCNavRail(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
//    width: Dp
) {
    NavigationRail(
        modifier = modifier.backgroundVertGradient(),
        containerColor = Color.Transparent,
        header = @Composable {},
        windowInsets = WindowInsets(0,0,0,0)
    ) {
        destinations.forEach {destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            NavigationRailItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    val icon = if (selected) destination.selectedIcon
                    else destination.unselectedIcon
                    when (icon) {
                        is DrawableResourceIcon -> Icon(
                            painter = painterResource(id = icon.id),
                            contentDescription = null,
//                            modifier = Modifier.requiredSize(itemSize)
                        )

                        is ImageVectorIcon -> Icon(
                            imageVector = icon.imageVector,
                            contentDescription = null,
//                            modifier = Modifier.requiredSize(itemSize)
                        )
                    }
                },
                modifier = modifier,
                label = { Text(stringResource(destination.iconTextId)) },
                alwaysShowLabel = true,
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = OdcNavigationDefaults.navigationSelectedItemColor(),
                    unselectedIconColor = OdcNavigationDefaults.navigationContentColor(),
                    selectedTextColor = OdcNavigationDefaults.navigationSelectedItemColor(),
                    unselectedTextColor = OdcNavigationDefaults.navigationContentColor(),
                    indicatorColor = OdcNavigationDefaults.navigationIndicatorColor(),
                ),
            )

        }

    }
}

@Preview
@Composable
private fun OdcNavRailPreview() {

}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false


fun Modifier.backgroundVertGradient(gradientColors: GradientColors = GradientColors.ColorSet1) =
    background(
        brush = Brush.linearGradient(
            colors = gradientColors.asList(), start = Offset(0f, 0f), end = Offset.Infinite
        )
    )