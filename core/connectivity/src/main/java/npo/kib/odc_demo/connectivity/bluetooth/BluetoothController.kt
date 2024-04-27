package npo.kib.odc_demo.connectivity.bluetooth

import androidx.activity.result.ActivityResultRegistry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import npo.kib.odc_demo.model.CustomBluetoothDevice

interface BluetoothController {

    companion object {
        const val SERVICE_UUID = "133f71c6-b7b6-437e-8fd1-d2f59cc76066"
        const val DEVICE_NAME_PREFIX = "##o*d&c\$a%p^p##"
    }

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
    suspend fun setDeviceName(newName: String): Boolean
    fun getDeviceName(): String
}