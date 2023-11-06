package npo.kib.odc_demo.feature_app.presentation.common

import android.Manifest
import android.graphics.Color.TRANSPARENT
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import dagger.hilt.android.AndroidEntryPoint
import npo.kib.odc_demo.feature_app.presentation.common.navigation.TopLevelDestination
import npo.kib.odc_demo.feature_app.presentation.common.ui.ODCApp
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.ODCBottomBar
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.ODCTopBar
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveViewModelNew
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.HomeScreen
import npo.kib.odc_demo.ui.DevicePreviews
import npo.kib.odc_demo.ui.ThemePreviews
import npo.kib.odc_demo.ui.theme.ODCAppTheme
import javax.inject.Inject

val LocalReceiveViewModelNewFactory =
    compositionLocalOf<ReceiveViewModelNew.Factory?> { null }

val LocalAppBluetoothPermissions: ProvidableCompositionLocal<List<String>> =
    compositionLocalOf { bluetoothPermissionsList }

private val bluetoothPermissionsList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_ADVERTISE
    )
} else listOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.BLUETOOTH,
    Manifest.permission.BLUETOOTH_ADMIN,
)

@AndroidEntryPoint
class MainActivityCompose : ComponentActivity() {

    @Inject
    lateinit var receiveViewModelNewFactory: ReceiveViewModelNew.Factory
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        Log.i(null, "Created")
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
                CompositionLocalProvider(
                    LocalReceiveViewModelNewFactory provides receiveViewModelNewFactory,
                    LocalAppBluetoothPermissions provides bluetoothPermissionsList
                ) {
                    ODCApp(
                        /* windowSizeClass = calculateWindowSizeClass(this) */
                    )
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
                ODCBottomBar(
                    destinations = TopLevelDestination.values().asList(),
                    {},
                    currentDestination = null,
                    modifier = Modifier/*.height(bottomBarHeightPercentage)*/
                )
            }) { paddingValues ->
                BoxWithConstraints(modifier = Modifier.padding(paddingValues = paddingValues)) {
                    HomeScreen(modifier = Modifier, onHistoryClick = {})

                }
            }
        }
    }
}

