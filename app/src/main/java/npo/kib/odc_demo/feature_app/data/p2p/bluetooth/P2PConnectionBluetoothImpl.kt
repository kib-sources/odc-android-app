package npo.kib.odc_demo.feature_app.data.p2p.bluetooth

import android.bluetooth.BluetoothSocket
import android.content.Context
import androidx.activity.result.ActivityResultRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import npo.kib.odc_demo.feature_app.domain.model.connection_status.ConnectingStatus
import npo.kib.odc_demo.feature_app.domain.model.connection_status.SearchingStatus
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnectionBidirectionalBluetooth
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.BluetoothController
import java.io.InputStream
import javax.inject.Inject

class P2PConnectionBluetoothImpl @Inject constructor(
    private val bluetoothController: BluetoothController, private val context: Context
) : P2PConnectionBidirectionalBluetooth {

    private val _connectionResult: MutableStateFlow<ConnectingStatus> = MutableStateFlow(
        ConnectingStatus.NoConnection
    )

    @Deprecated(
        "use startAdvertising(registry: ActivityResultRegistry, duration: Int)",
        ReplaceWith("startAdvertising(registry: ActivityResultRegistry, duration: Int)")
    )
    override fun startAdvertising() = Unit

    @Deprecated(
        "use stopAdvertising(registry: ActivityResultRegistry)",
        ReplaceWith("stopAdvertising(registry: ActivityResultRegistry)")
    )
    override fun stopAdvertising() = Unit

    override fun startAdvertising(registry: ActivityResultRegistry, duration: Int) {
        bluetoothController.startAdvertising(registry, duration)
    }

    override fun stopAdvertising(registry: ActivityResultRegistry) {
        bluetoothController.stopAdvertising(registry)
    }

    override val connectionResult = _connectionResult.asStateFlow()

    private val _searchingStatusFlow: MutableStateFlow<SearchingStatus> = MutableStateFlow(
        SearchingStatus.NONE
    )
    override val searchingStatusFlow = _searchingStatusFlow.asStateFlow()

    private val _receivedBytes = MutableSharedFlow<ByteArray>()
    override val receivedBytes = _receivedBytes.asSharedFlow()

    private val scope = CoroutineScope(Dispatchers.IO)


    private var connectedSocket: BluetoothSocket? = null


    override fun startDiscovery() {/*        scope.launch {
                    if (!adapter.isEnabled) {
                        return@launch
                    }

                    val device = adapter.getRemoteDevice("AA:AA:AA:AA:AA:AA")
                    val bondingResult = runCatching { device.createBond() }
                    if (bondingResult.isFailure || !bondingResult.getOrDefault(false)) {
                        return@launch
                    }

                    val socket: BluetoothSocket = device.createRfcommSocketToServiceRecord(serviceUUID)
                    val connectionResult = runCatching { socket.connect() }
                    if (connectionResult.isFailure) {
                        return@launch
                    }

                    connectedSocket = socket
                    readInputStream(socket.inputStream)
                }*/
        bluetoothController.startDiscovery()
    }

    override fun stopDiscovery() {
        bluetoothController.stopDiscovery()
//        _searchingStatusFlow.update { SearchingStatus.NONE }
    }

    override fun sendBytes(bytes: ByteArray) {
        connectedSocket?.runCatching {
            val msg = (bytes.decodeToString() + "\n").encodeToByteArray()
            outputStream.write(msg)
        }
    }

    private suspend fun readInputStream(inputStream: InputStream) {
        val reader = inputStream.bufferedReader()
        connectedSocket?.runCatching {
            while (isConnected) {
                val line = reader.readLine()
                _receivedBytes.emit(line.encodeToByteArray())
            }
        }
    }

    override fun acceptConnection() = Unit

    override fun rejectConnection() = Unit
    override fun connectToDevice() {
        TODO("Not yet implemented")
    }
}