package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.p2p_screens.nearby_screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.nearby.connection.ConnectionInfo
import npo.kib.odc_demo.R
import npo.kib.odc_demo.feature_app.presentation.SwitcherInterface

// базовый класс для фрагментов, где предполагается использование Nearby Connections API
abstract class BaseNearbyFragment : Fragment() {

    protected abstract val viewModel: BaseNearbyViewModel

    protected lateinit var mController: SwitcherInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (context !is SwitcherInterface)
            throw RuntimeException("$context must implement SwitcherInterface")

        mController = context as SwitcherInterface
    }

    protected fun showConnectionDialog(info: ConnectionInfo) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.accept_connection_to) + info.endpointName)
            .setMessage(getString(R.string.make_sure) + info.authenticationDigits)
            .setPositiveButton(
                getString(R.string.receive)
            ) { _, _ ->  // The user confirmed, so we can accept the connection.
                viewModel.acceptConnection()
            }
            .setNegativeButton(
                getString(R.string.cancel)
            ) { _, _ ->  // The user canceled, so we should reject the connection.
                viewModel.rejectConnection()
            }
            //           .setIcon(ContextCompat.getDrawable(view.context, R.drawable.ic_dialog_alert))
            .show()
            .setCanceledOnTouchOutside(false)
    }

    //Runtime permissions

    protected abstract fun onPermissionGranted()
    protected abstract fun onPermissionRejected()

    protected fun askForPermissions(): Boolean {
        val locationPermission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACCESS_FINE_LOCATION
            else Manifest.permission.ACCESS_COARSE_LOCATION

        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val bluetoothAdvertisePermission = Manifest.permission.BLUETOOTH_ADVERTISE
            val bluetoothConnectPermission = Manifest.permission.BLUETOOTH_CONNECT
            val bluetoothScanPermission = Manifest.permission.BLUETOOTH_SCAN
            arrayOf(
                locationPermission,
                bluetoothAdvertisePermission,
                bluetoothConnectPermission,
                bluetoothScanPermission
            )
        } else {
            arrayOf(locationPermission)
        }


        if (!isPermissionsAllowed()) {
            permissions.forEach { permission ->
                if (shouldShowRequestPermissionRationale(permission)) {
                    showPermissionDeniedDialog()
                    return false
                }
            }
            activityResultLauncher.launch(permissions)
            return false
        }
        return true
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.permissions_denied))
            .setMessage(getString(R.string.allow_perm_in_settings))
            .setPositiveButton(getString(R.string.settings)) { _, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", requireActivity().application.packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
            .setCanceledOnTouchOutside(false)
    }

    private val activityResultLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.values.all { it == true }) {
                onPermissionGranted()
            } else {
                onPermissionRejected()
                showPermissionDeniedDialog()
            }
        }

    private fun isPermissionsAllowed(): Boolean {
        var isAllowed = ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            isAllowed = isAllowed &&
                    ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.BLUETOOTH_ADVERTISE
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.BLUETOOTH_SCAN
                    ) == PackageManager.PERMISSION_GRANTED
        }
        return isAllowed
    }
}