package npo.kib.odc_demo.feature_app.domain.p2p.bluetooth

import androidx.activity.result.ActivityResultRegistry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import npo.kib.odc_demo.feature_app.data.p2p.bluetooth.BluetoothState
import npo.kib.odc_demo.feature_app.domain.model.connection_status.BluetoothConnectionResult
import kotlin.ByteArray

interface BluetoothController {

    val bluetoothStateColdFlow: Flow<BluetoothState>
    val errors: SharedFlow<String>

    fun startDiscovery()
    fun stopDiscovery()

    fun startAdvertising(registry: ActivityResultRegistry, duration: Int, callback: (Int?) -> Unit)
    fun stopAdvertising(registry: ActivityResultRegistry)

    fun startBluetoothServerAndGetFlow(): Flow<BluetoothConnectionResult>
    fun connectToDevice(device: CustomBluetoothDevice): Flow<BluetoothConnectionResult>

    suspend fun trySendBytes(bytes: ByteArray): ByteArray?

    fun closeConnection()
    fun reset()
}