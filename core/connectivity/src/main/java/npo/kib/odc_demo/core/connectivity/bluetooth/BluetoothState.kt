package npo.kib.odc_demo.core.connectivity.bluetooth

import npo.kib.odc_demo.core.connectivity.bluetooth.BluetoothConnectionStatus.DISCONNECTED
import npo.kib.odc_demo.core.model.CustomBluetoothDevice

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