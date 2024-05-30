package npo.kib.odc_demo

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import kotlinx.coroutines.CoroutineScope
import npo.kib.odc_demo.feature.home.navigation.homeRoute
import npo.kib.odc_demo.feature.home.navigation.navigateToHomeGraph
import npo.kib.odc_demo.navigation.TopLevelDestination
import npo.kib.odc_demo.navigation.TopLevelDestination.HOME
import npo.kib.odc_demo.navigation.TopLevelDestination.SETTINGS
import npo.kib.odc_demo.feature.settings.navigation.navigateToSettingsScreen
import npo.kib.odc_demo.feature.settings.navigation.settingsRoute


@Composable
fun rememberODCAppState(
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    windowSizeClass: WindowSizeClass
): ODCAppState =
    remember(navController) {
        ODCAppState(
            navController,
            coroutineScope,
            windowSizeClass
        )
    }


@Stable
class ODCAppState(
    val navController: NavHostController,
    private val coroutineScope: CoroutineScope,
    val windowSizeClass: WindowSizeClass
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            homeRoute -> HOME
            settingsRoute -> SETTINGS
            else -> null
        }

    //To be able to disable bottom bar on log-in screen in the future
    val shouldShowBottomBar: Boolean
        get() = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    val shouldShowNavRail: Boolean
        get() = !shouldShowBottomBar


    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries


    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        //can surround everything with trace(){} from androidx.tracing here to log the navigation events for debugging
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
//                Is not really needed for the app's top-level screen structure
//                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // re-selecting the same item
            launchSingleTop = true
//             Restore state when re-selecting a previously selected item. Not needed because of how top-level screens are structured
//             and how changing data on one screen may break the other screen's ongoing tasks
//             restoreState = true
        }
        when (topLevelDestination) {
            HOME -> navController.navigateToHomeGraph(topLevelNavOptions)
            SETTINGS -> navController.navigateToSettingsScreen(topLevelNavOptions)
        }
    }

}