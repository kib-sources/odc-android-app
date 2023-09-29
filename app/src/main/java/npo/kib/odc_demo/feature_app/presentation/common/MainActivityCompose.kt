package npo.kib.odc_demo.feature_app.presentation.common

import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import dagger.hilt.android.AndroidEntryPoint
import npo.kib.odc_demo.feature_app.presentation.common.ui.ODCApp
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.ODCBottomBar
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.ODCTopBar
import npo.kib.odc_demo.feature_app.presentation.navigation.TopLevelDestination
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.HomeScreen
import npo.kib.odc_demo.ui.DevicePreviews
import npo.kib.odc_demo.ui.ThemePreviews
import npo.kib.odc_demo.ui.theme.ODCAppTheme


@AndroidEntryPoint
class MainActivityCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {

            val darkTheme = isSystemInDarkTheme()

            // Update the dark content of the system bars to match the theme
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.light(TRANSPARENT, TRANSPARENT),
                    navigationBarStyle = SystemBarStyle.light(TRANSPARENT, TRANSPARENT)
                )
                onDispose {}
            }


            ODCAppTheme(useDarkTheme = darkTheme) {
                ODCApp(
                    /* windowSizeClass = calculateWindowSizeClass(this) */
                )
            }
        }
    }
}


//A function to ensure to not navigate to a screen if we are on that screen already
private fun NavController.navigateToScreenOnce(route: String) {
    if (this.currentDestination?.route != route) this.navigate(route)
}


private fun NavController.getCurrentDestination(): String =
    currentDestination?.route ?: "null_route"



@ThemePreviews
@DevicePreviews
@Composable
private fun HomeScreenPreview() {
    ODCAppTheme {
        BoxWithConstraints(propagateMinConstraints = false) {
            val topBarHeightPercentage = maxHeight * 0.1f
            val topBarWithBalanceBlockHeightPercentage = maxHeight * 0.25f
            val bottomBarHeightPercentage = maxHeight * 0.07f


            Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                ODCTopBar(
                    modifier = Modifier/*.height(topBarHeightPercentage)*/
                )
            }, bottomBar = {
                ODCBottomBar(destinations = TopLevelDestination.values().asList(), {}, currentDestination = null,
                             modifier = Modifier/*.height(bottomBarHeightPercentage)*/
                )
            }) { paddingValues ->
                BoxWithConstraints(modifier = Modifier.padding(paddingValues = paddingValues)) {
                    HomeScreen(modifier = Modifier)

                }
            }
        }
    }
}

