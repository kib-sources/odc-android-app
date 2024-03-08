package npo.kib.odc_demo.feature_app.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import npo.kib.odc_demo.feature_app.presentation.common.navigation.TopLevelDestination
import npo.kib.odc_demo.ui.DevicePreviews
import npo.kib.odc_demo.ui.Icon
import npo.kib.odc_demo.ui.ThemePreviews
import npo.kib.odc_demo.ui.theme.CustomColors

@Composable
fun ODCBottomBar(destinations: List<TopLevelDestination>,
                 onNavigateToDestination: (TopLevelDestination) -> Unit,
                 currentDestination: NavDestination?,
                 modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStartPercent = 50, topEndPercent = 50)) {
        NavigationBar(
            modifier = Modifier.background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        CustomColors.Gradient_Color_1, CustomColors.Gradient_Color_2),
                    start = Offset(0f, 0f),
                    end = Offset.Infinite)), containerColor = Color.Transparent) {

            destinations.forEach { destination ->
                val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
                NavigationBarItem(selected = selected,
                                  onClick = { onNavigateToDestination(destination) },
                                  icon = {
                                      val icon = if (selected) destination.selectedIcon
                                      else destination.unselectedIcon
                                      when (icon) {
                                          is Icon.DrawableResourceIcon -> Icon(
                                              painter = painterResource(id = icon.id),
                                              contentDescription = null)

                                          is Icon.ImageVectorIcon -> Icon(
                                              imageVector = icon.imageVector,
                                              contentDescription = null)
                                      }

                                  },
                                  label = { /*Text(stringResource(id = destination.iconTextId))*/ }
                )
            }
        }
    }
}
@ThemePreviews
@DevicePreviews
@Composable
private fun ODCBottomBarPreview() {
    ODCBottomBar(destinations = TopLevelDestination.values().asList(),
                 onNavigateToDestination = {}, currentDestination = null,
                 modifier = Modifier/*.height(70.dp)*/)
}


private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false