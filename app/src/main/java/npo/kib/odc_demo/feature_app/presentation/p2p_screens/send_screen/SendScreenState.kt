package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen

import npo.kib.odc_demo.feature_app.domain.model.user.AppUser
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.CustomBluetoothDevice

data class SendScreenState(
    val scannedDevices : List<CustomBluetoothDevice> = emptyList(),
    val pairedDevices: List<CustomBluetoothDevice> = emptyList(),
    val connectedDevice: CustomBluetoothDevice,
    val isConnected : Boolean,
    var localUser : AppUser,
    var remoteUser : AppUser
)

