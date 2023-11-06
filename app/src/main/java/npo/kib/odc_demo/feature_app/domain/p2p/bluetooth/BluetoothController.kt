package npo.kib.odc_demo.feature_app.domain.p2p.bluetooth

import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultRegistry
import androidx.lifecycle.DefaultLifecycleObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import npo.kib.odc_demo.feature_app.domain.model.connection_status.ConnectingStatus

interface BluetoothController {
    val isConnected: StateFlow<Boolean>
    val scannedDevices: StateFlow<List<CustomBluetoothDevice>>
    val bondedDevices: StateFlow<List<CustomBluetoothDevice>>
    val errors: SharedFlow<String>

    fun startDiscovery()
    fun stopDiscovery()

    fun startBluetoothServer(): Flow<ConnectionResult>
    fun connectToDevice(device: CustomBluetoothDevice): Flow<ConnectingStatus.ConnectionResult>

    suspend fun trySendData(data: ByteArray): BluetoothDataPacket?

    fun startAdvertising(registry: ActivityResultRegistry, duration: Int)

    fun stopAdvertising(registry: ActivityResultRegistry)

    fun closeConnection()
    fun release()
}