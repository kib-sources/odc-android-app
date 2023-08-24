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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.AppTopBar
import npo.kib.odc_demo.feature_app.presentation.home_screen.HomeScreen
import npo.kib.odc_demo.feature_app.presentation.navigation.BottomNavigationBar
import npo.kib.odc_demo.feature_app.presentation.navigation.Screen
import npo.kib.odc_demo.feature_app.presentation.send_screen.SendScreen
import npo.kib.odc_demo.feature_app.presentation.settings_screen.SettingsScreen
import npo.kib.odc_demo.ui.theme.ODCAppTheme


@AndroidEntryPoint
class MainActivityCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ODCAppTheme {
                val navController = rememberNavController()

                var bottomBarSelectedItem by remember { mutableStateOf(Screen.HomeScreen.route) }

                BoxWithConstraints {
                    val topBarHeightPercentage = maxHeight * 0.1f
                    val bottomBarHeightPercentage = maxHeight * 0.07f

                    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                        AppTopBar(modifier = Modifier.height(topBarHeightPercentage))
                    }, bottomBar = {
                        BottomNavigationBar(/*getCurrentDestination =
                                            navController::getCurrentDestination,*/
//                                            { Screen.SettingsScreen.route },
                                            selectedItem = bottomBarSelectedItem,
                                            updateSelectedItem = { bottomBarSelectedItem = it},
                            onClickHome = {
                                navController.navigateToScreenOnce(
                                    Screen.HomeScreen.route)
                            }, onClickSettings = {
                                navController.navigateToScreenOnce(
                                    Screen.SettingsScreen.route)
                            }, modifier = Modifier.height(
                                bottomBarHeightPercentage))
                    }) { paddingValues ->
                        BoxWithConstraints(modifier = Modifier.padding(paddingValues = paddingValues)) {

                            NavHost(
                                navController = navController,
                                startDestination = Screen.HomeScreen.route) {
                                composable(Screen.HomeScreen.route) {
                                    HomeScreen(onClickButton3 = {
                                        bottomBarSelectedItem = Screen.P2PScreen.SendNFC.route
                                        navController.navigateToScreenOnce(
                                            route = Screen.P2PScreen.SendNFC.route)
                                    })
                                }
                                composable(Screen.SettingsScreen.route) {
                                    SettingsScreen(onBackClick = navController::navigateUp)
                                }
                                composable(Screen.HistoryScreen.route) {

                                }
                                composable(Screen.P2PScreen.SendNFC.route) {
                                    SendScreen(
                                        onClick = navController::navigateUp)
                                }

                            composable(Screen.P2PScreen.RequestNFC.route) {

                            }
                            composable(Screen.P2PScreen.TopUpNFC.route) {

                            }
                            }
                            //log in/register todo
//                        composable(Screen.P2PScreen.SendNFC.route) {
//                            SettingsScreen(onBackClick = {})
//                        }
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


private fun NavController.getCurrentDestination(): String {
    return if (currentDestination?.route == null) "null_route" else currentDestination!!.route!!
}


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
                BottomNavigationBar(/*getCurrentDestination = { Screen.HomeScreen.route },*/
                                    selectedItem = Screen.HomeScreen.route,
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

