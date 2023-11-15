package npo.kib.odc_demo.feature_app.domain.p2p.bluetooth

import androidx.activity.result.ActivityResultRegistry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import npo.kib.odc_demo.feature_app.domain.model.connection_status.BluetoothConnectionStatus
import kotlin.ByteArray

interface BluetoothController {
    val isConnected: StateFlow<Boolean>
    val scannedDevices: StateFlow<List<CustomBluetoothDevice>>
    val bondedDevices: StateFlow<List<CustomBluetoothDevice>>
    val errors: SharedFlow<String>

    fun startDiscovery()
    fun stopDiscovery()

    fun startBluetoothServerAndGetFlow(): Flow<BluetoothConnectionStatus>
    fun connectToDevice(device: CustomBluetoothDevice): Flow<BluetoothConnectionStatus>

    suspend fun trySendBytes(bytes: ByteArray): ByteArray?

    fun startAdvertising(registry: ActivityResultRegistry, duration: Int, callback: (Int?) -> Unit)

    fun stopAdvertising(registry: ActivityResultRegistry)

    fun closeConnection()
    fun reset()
}