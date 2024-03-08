package npo.kib.odc_demo.feature_app.presentation.common

import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import dagger.hilt.android.AndroidEntryPoint
import npo.kib.odc_demo.feature_app.data.permissions.PermissionProvider.LocalAppBluetoothPermissions
import npo.kib.odc_demo.feature_app.data.permissions.PermissionProvider.bluetoothPermissionsList
import npo.kib.odc_demo.ui.theme.ODCAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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