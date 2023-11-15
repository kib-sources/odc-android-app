package npo.kib.odc_demo.feature_app.data.p2p.bluetooth

import android.bluetooth.BluetoothSocket
import android.content.Context
import androidx.activity.result.ActivityResultRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import npo.kib.odc_demo.feature_app.domain.model.connection_status.BluetoothConnectionStatus
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.BluetoothController
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.CustomBluetoothDevice
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.P2PConnectionBluetooth
import javax.inject.Inject

class P2PConnectionBluetoothImpl @Inject constructor(
    private val bluetoothController: BluetoothController, private val context: Context
) : P2PConnectionBluetooth {

    private val _connectionStatus: MutableStateFlow<BluetoothConnectionStatus.NoConnection> =
        MutableStateFlow(
            BluetoothConnectionStatus.NoConnection
        )

    override val connectionStatus = _connectionStatus.asStateFlow()


    private val _receivedBytes = MutableSharedFlow<ByteArray>()
    override val receivedBytes = _receivedBytes.asSharedFlow()

    private val scope = CoroutineScope(Dispatchers.IO)


    private var connectedSocket: BluetoothSocket? = null

    @Deprecated(
        "use startAdvertising(registry: ActivityResultRegistry, duration: Int, callback: (Int?) -> Unit)",
        ReplaceWith("startAdvertising(registry: ActivityResultRegistry, duration: Int)"),
        level = DeprecationLevel.HIDDEN
    )
    override fun startAdvertising() = Unit

    @Deprecated(
        "use other option",
        ReplaceWith("stopAdvertising(registry: ActivityResultRegistry)"),
        level = DeprecationLevel.HIDDEN
    )
    override fun stopAdvertising() = Unit

    override fun startAdvertising(
        registry: ActivityResultRegistry, duration: Int, callback: (Int?) -> Unit
    ) {
        bluetoothController.startAdvertising(registry, duration, callback)
    }

    override fun stopAdvertising(registry: ActivityResultRegistry) {
        bluetoothController.stopAdvertising(registry)
    }

    override fun startBluetoothServerAndGetFlow(): Flow<BluetoothConnectionStatus> {
        return bluetoothController.startBluetoothServerAndGetFlow()
    }

    override fun startDiscovery() {
        bluetoothController.startDiscovery()
    }

    override fun stopDiscovery() {
        bluetoothController.stopDiscovery()
    }

    override fun acceptConnection() = Unit

    override fun rejectConnection() = Unit


    override fun connectToDevice(device: CustomBluetoothDevice): Flow<BluetoothConnectionStatus> =
        bluetoothController.connectToDevice(device)

    override suspend fun sendBytes(bytes: ByteArray): ByteArray? {
        return bluetoothController.trySendBytes(bytes = bytes)
    }

    override fun closeConnection() {
        bluetoothController.closeConnection()
    }

    override fun reset() {
        bluetoothController.reset()
    }

}