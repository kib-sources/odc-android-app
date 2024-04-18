package npo.kib.odc_demo.feature_app.data.permissions

import android.Manifest
import android.os.Build
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf

object PermissionProvider {
    val LocalAppBluetoothPermissions: ProvidableCompositionLocal<List<String>> =
        staticCompositionLocalOf { bluetoothPermissionsList }

    val bluetoothPermissionsList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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

    val LocalAppNFCPermissions: ProvidableCompositionLocal<List<String>> =
        staticCompositionLocalOf { nfcPermissionsList }

    val nfcPermissionsList: List<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(

        )
    } else listOf(

    )

}