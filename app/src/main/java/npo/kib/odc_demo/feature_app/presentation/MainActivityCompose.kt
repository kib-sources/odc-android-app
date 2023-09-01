package npo.kib.odc_demo.feature_app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.AppTopBar
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.HomeScreen
import npo.kib.odc_demo.feature_app.presentation.navigation.BottomNavigationBar
import npo.kib.odc_demo.feature_app.presentation.navigation.ScreenRoutes
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.p2p_screens.send_screen.SendScreen
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.settings_screen.SettingsScreen
import npo.kib.odc_demo.ui.theme.ODCAppTheme


@AndroidEntryPoint
class MainActivityCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ODCAppTheme {
                val navController = rememberNavController()

                var bottomBarSelectedItem by rememberSaveable { mutableStateOf(ScreenRoutes.HomeScreen.route) }

                BoxWithConstraints {
                    val topBarHeightPercentage = maxHeight * 0.1f
                    val bottomBarHeightPercentage = maxHeight * 0.07f

                    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                        AppTopBar(modifier = Modifier.height(topBarHeightPercentage))
                    }, bottomBar = {
                        BottomNavigationBar(selectedItem = bottomBarSelectedItem,
                                            updateSelectedItem = { bottomBarSelectedItem = it },
                                            onClickHome = {
                                                navController.navigateToScreenOnce(
                                                    ScreenRoutes.HomeScreen.route)
                                            }, onClickSettings = {
                                navController.navigateToScreenOnce(
                                    ScreenRoutes.SettingsScreen.route)
                            }, modifier = Modifier.height(
                                bottomBarHeightPercentage))
                    }) { paddingValues ->
                        BoxWithConstraints(modifier = Modifier.padding(paddingValues = paddingValues)) {

                            NavHost(
                                navController = navController,
                                startDestination = ScreenRoutes.HomeScreen.route) {
                                composable(ScreenRoutes.HomeScreen.route) {
                                    HomeScreen(navController = navController, onClickButton3 = {
                                        bottomBarSelectedItem = ScreenRoutes.P2PScreen.SendNFC.route
                                        navController.navigateToScreenOnce(
                                            route = ScreenRoutes.P2PScreen.SendNFC.route)
                                    })
                                }
                                composable(ScreenRoutes.SettingsScreen.route) {
                                    SettingsScreen(onBackClick = navController::navigateUp)
                                }
                                composable(ScreenRoutes.HistoryScreen.route) {

                                }
                                composable(ScreenRoutes.P2PScreen.SendNFC.route) {
                                    SendScreen(
                                        onClick = navController::navigateUp)
                                }

                                composable(ScreenRoutes.P2PScreen.RequestNFC.route) {

                                }
                                composable(ScreenRoutes.P2PScreen.ATM.route) {

                                }
                            }
                            //log in/register todo
//
                        }
                    }
                }
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


@Preview(showSystemUi = false)
@Composable
fun HomeScreenPreview() {
    ODCAppTheme {
        BoxWithConstraints(propagateMinConstraints = false) {
            val topBarHeightPercentage = maxHeight * 0.1f
            val bottomBarHeightPercentage = maxHeight * 0.07f


            Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                AppTopBar(
                    modifier = Modifier.height(topBarHeightPercentage)
//                            .padding(15.dp)
                         )
            }, bottomBar = {
                BottomNavigationBar(/*getCurrentDestination = { ScreenRoutes.HomeScreen.route },*/
                                    selectedItem = ScreenRoutes.HomeScreen.route,
                                    updateSelectedItem = {},
                                    modifier = Modifier.height(bottomBarHeightPercentage),
                                    onClickHome = {},
                                    onClickSettings = {})
            }) { paddingValues ->
                BoxWithConstraints(modifier = Modifier.padding(paddingValues = paddingValues)) {
                    HomeScreen(modifier = Modifier)

                }
            }
        }
    }
}

