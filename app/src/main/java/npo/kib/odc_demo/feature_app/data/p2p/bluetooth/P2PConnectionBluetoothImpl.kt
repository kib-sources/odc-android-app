package npo.kib.odc_demo.feature_app.data.p2p.bluetooth

import android.bluetooth.BluetoothSocket
import android.content.Context
import androidx.activity.result.ActivityResultRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.domain.model.connection_status.BluetoothConnectionStatus
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.BluetoothController
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.CustomBluetoothDevice
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.P2PConnectionBluetooth
import javax.inject.Inject

class P2PConnectionBluetoothImpl @Inject constructor(
    private val bluetoothController: BluetoothController, private val context: Context
) : P2PConnectionBluetooth {

    private val _connectionStatus: MutableStateFlow<BluetoothConnectionStatus> =
        MutableStateFlow(BluetoothConnectionStatus.NoConnection)
    override val connectionStatus = _connectionStatus.asStateFlow()

    private val _receivedBytes = Channel<ByteArray>(capacity = UNLIMITED)
    override val receivedBytes: Flow<ByteArray> = _receivedBytes.receiveAsFlow()


//    private val _receivedData: Channel<DataPacketVariant> = Channel(capacity = UNLIMITED)
//    val receivedData: Flow<DataPacketVariant> = _receivedData.receiveAsFlow()

    private var currentJob: Job? = null

    //todo use this instead of a job and pass a viewModelScope?
    override lateinit var scope: CoroutineScope

//    private val scope = CoroutineScope(Dispatchers.IO)


    private var connectedSocket: BluetoothSocket? = null

    override fun startAdvertising(
        registry: ActivityResultRegistry, duration: Int, callback: (Int?) -> Unit
    ) {
        bluetoothController.startAdvertising(registry, duration, callback)
    }

    override fun stopAdvertising(registry: ActivityResultRegistry) {
        bluetoothController.stopAdvertising(registry)
    }

    override fun startDiscovery() {
        bluetoothController.startDiscovery()
    }

    override fun stopDiscovery() {
        bluetoothController.stopDiscovery()
    }

    override fun acceptConnection() = Unit

    override fun rejectConnection() = Unit

    override fun startBluetoothServerAndGetBytesFlow()/*: Flow<BluetoothConnectionStatus>*/ {
        resetJob()

        currentJob = bluetoothController.startBluetoothServerAndGetFlow().onEach { status ->
            updateConnectionStatus(status)
            when (status) {
                is BluetoothConnectionStatus.TransferSucceeded -> {
                    _receivedBytes.send(status.bytes)
                }
                else -> {/* Nothing is required here */}
            }
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }
    override fun connectToDevice(device: CustomBluetoothDevice)/*: Flow<BluetoothConnectionStatus>*/ {
        resetJob()
        currentJob = bluetoothController.connectToDevice(device).onEach { status ->
            updateConnectionStatus(status)
            when (status) {
                is BluetoothConnectionStatus.TransferSucceeded -> {
                    _receivedBytes.send(status.bytes)
                }
                else -> {/* Nothing is required here */}
            }
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    override suspend fun sendBytes(bytes: ByteArray): ByteArray? {
        return bluetoothController.trySendBytes(bytes = bytes)
    }

    override fun closeConnection() {
        bluetoothController.closeConnection()
    }

    override fun reset() {
        bluetoothController.reset()
        resetJob()
    }

    private fun resetJob(){
        currentJob?.cancel()
        currentJob = null
    }

    private fun updateConnectionStatus(status: BluetoothConnectionStatus) {
        _connectionStatus.update { status }
    }

}