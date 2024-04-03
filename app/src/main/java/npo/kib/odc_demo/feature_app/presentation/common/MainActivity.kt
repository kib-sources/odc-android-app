package npo.kib.odc_demo.feature_app.presentation.common

import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.data.permissions.PermissionProvider.LocalAppBluetoothPermissions
import npo.kib.odc_demo.feature_app.data.permissions.PermissionProvider.bluetoothPermissionsList
import npo.kib.odc_demo.feature_app.domain.util.log
import npo.kib.odc_demo.feature_app.presentation.common.MainActivityUiState.Loading
import npo.kib.odc_demo.feature_app.presentation.common.components.ODCGradientButton
import npo.kib.odc_demo.feature_app.presentation.common.components.ODCPlainButton
import npo.kib.odc_demo.ui.theme.ODCAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

//    @Inject
//    lateinit var userPreferencesDataSource : DefaultDataStoreRepository

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        this.log("Created")

        var uiState: MainActivityUiState by mutableStateOf(Loading)

        // Update the uiState
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.onEach { uiState = it }.collect()
            }
        }

        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                Loading -> true
                else -> false
            }
        }

        setContent {
            val darkTheme = isSystemInDarkTheme()

            // Update the dark content of the system bars to match the theme
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    /* SystemBarStyle.light(TRANSPARENT, TRANSPARENT) or SystemBarStyle.dark(TRANSPARENT)*/
                    statusBarStyle = SystemBarStyle.auto(
                        TRANSPARENT,
                        TRANSPARENT,
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
//                        lightScrim,
//                        darkScrim,
                        TRANSPARENT,
                        TRANSPARENT,
                    ) { darkTheme }
                )
                onDispose {}
            }

            ODCAppTheme(useDarkTheme = darkTheme) {
                when (uiState) {
                    Loading -> {/* Splash screen is active */
                    }

                    MainActivityUiState.FailureConnectingToBank -> Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Failure registering wallet")
                        ODCGradientButton(
                            text = "Try again", onClick = viewModel::registerWalletWithBank
                        )
                    }

                    is MainActivityUiState.Success -> CompositionLocalProvider(
                        LocalAppBluetoothPermissions provides bluetoothPermissionsList
                    ) {
                        ODCApp(/* windowSizeClass = calculateWindowSizeClass(this)*/)
                    }
                }
            }
        }
    }
}

/**
 * The default light scrim, as defined by androidx and the platform:
 */
private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 */
private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)