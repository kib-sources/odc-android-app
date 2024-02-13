package npo.kib.odc_demo.feature_app.data.p2p.bluetooth

import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.CustomBluetoothDevice

data class BluetoothState(
    val isConnecting: Boolean = false,
    val isConnected: Boolean = false,
    val connectedDevice: CustomBluetoothDevice? = null,
    val scannedDevices: List<CustomBluetoothDevice> = emptyList(),
    val bondedDevices: List<CustomBluetoothDevice> = emptyList()
)
