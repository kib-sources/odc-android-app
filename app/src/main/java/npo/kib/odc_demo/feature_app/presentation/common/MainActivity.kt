package npo.kib.odc_demo.feature_app.presentation.common

import android.graphics.Color.TRANSPARENT
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
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import npo.kib.odc_demo.feature_app.data.permissions.PermissionProvider.LocalAppBluetoothPermissions
import npo.kib.odc_demo.feature_app.data.permissions.PermissionProvider.bluetoothPermissionsList
import npo.kib.odc_demo.feature_app.presentation.common.navigation.TopLevelDestination
import npo.kib.odc_demo.feature_app.presentation.common.ui.ODCApp
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.ODCBottomBar
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.ODCTopBar
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveViewModel
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveViewModel.Companion.LocalReceiveViewModelFactory
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.HomeScreen
import npo.kib.odc_demo.ui.DevicePreviews
import npo.kib.odc_demo.ui.ThemePreviews
import npo.kib.odc_demo.ui.theme.ODCAppTheme
import javax.inject.Inject
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var receiveViewModelFactory: ReceiveViewModel.Factory
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
                    LocalReceiveViewModelFactory provides receiveViewModelFactory,
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
                    destinations = TopLevelDestination.entries,
                    {},
                    currentDestination = null,
                    modifier = Modifier/*.height(bottomBarHeightPercentage)*/
                )
            }) { paddingValues ->
                BoxWithConstraints(modifier = Modifier.padding(paddingValues = paddingValues)) {
                    val a = maxHeight
                    HomeScreen(modifier = Modifier, onHistoryClick = {})
                }
            }
        }
    }
}

