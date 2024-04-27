package npo.kib.odc_demo.connectivity.bluetooth

import npo.kib.odc_demo.connectivity.bluetooth.BluetoothConnectionStatus.DISCONNECTED
import npo.kib.odc_demo.model.CustomBluetoothDevice

data class BluetoothState(
    val connectionStatus: BluetoothConnectionStatus = DISCONNECTED,
    val connectedDevice: CustomBluetoothDevice? = null,
    val scannedDevices: List<CustomBluetoothDevice> = emptyList(),
    val bondedDevices: List<CustomBluetoothDevice> = emptyList()
)

enum class BluetoothConnectionStatus {
    DISCONNECTED,
    ADVERTISING,
    DISCOVERING,
    CONNECTING,
    CONNECTED
}