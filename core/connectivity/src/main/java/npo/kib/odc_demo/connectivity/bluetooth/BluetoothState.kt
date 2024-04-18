package npo.kib.odc_demo.feature_app.data.p2p.bluetooth

import npo.kib.odc_demo.feature_app.data.p2p.bluetooth.BluetoothConnectionStatus.DISCONNECTED
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.CustomBluetoothDevice

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