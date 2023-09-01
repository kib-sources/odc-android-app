package npo.kib.odc_demo.feature_app.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import npo.kib.odc_demo.R
import npo.kib.odc_demo.ui.theme.CustomColors

@Composable
fun BottomNavigationBar(selectedItem: String,
                        updateSelectedItem : (updateTo : String) -> Unit,
                        onClickHome: () -> Unit,
                        onClickSettings: () -> Unit,
                        modifier: Modifier = Modifier) {

//    var selectedItem by remember {
//        mutableStateOf(NavigationItem.Home.route)
//    }
    Surface(
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(topStartPercent = 50, topEndPercent = 50)) {
        NavigationBar(
            modifier = Modifier.background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        CustomColors.Gradient_Color_1, CustomColors.Gradient_Color_2),
                    start = Offset(0f, 0f),
                    end = Offset.Infinite)), containerColor = Color.Transparent) {
            //Home
            NavigationBarItem(modifier = Modifier.weight(1f),
                              selected = (selectedItem == ScreenRoutes.HomeScreen.route),
                              onClick = {
//                                  selectedItem = ScreenRoutes.HomeScreen.route
                                  updateSelectedItem(ScreenRoutes.HomeScreen.route)
                                  onClickHome()
                              },
                              alwaysShowLabel = false,
//                          label = { Text("Home") },
                              icon = {
                                  Icon(
                                      painter = painterResource(id = R.drawable.home_icon),
                                      contentDescription = null,
                                      tint = Color.White)
                              })
            Column(modifier = Modifier.weight(0.5f)) {

            }
            //Settings
            NavigationBarItem(modifier = Modifier.weight(1f),
                              selected = (selectedItem == ScreenRoutes.SettingsScreen.route),
                              onClick = {
//                                  selectedItem = ScreenRoutes.SettingsScreen.route
                                  updateSelectedItem(ScreenRoutes.SettingsScreen.route)
                                  onClickSettings()
                              },
                              alwaysShowLabel = false,
//                          label = { Text("Settings") },
                              icon = {
                                  Icon(
                                      painter = painterResource(id = R.drawable.settings_icon),
                                      contentDescription = null,
                                      tint = Color.White)
                              })
        }
    }
}

@Preview
@Composable
fun BottomNavigationBarPreview() {
    BottomNavigationBar(
        selectedItem = ScreenRoutes.HomeScreen.route,
        updateSelectedItem = {},
        onClickHome = {},
        onClickSettings = {},
        modifier = Modifier.height(70.dp))
}