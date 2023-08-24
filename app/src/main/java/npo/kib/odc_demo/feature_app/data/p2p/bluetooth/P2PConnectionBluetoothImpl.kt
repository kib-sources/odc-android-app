package npo.kib.odc_demo.feature_app.data.p2p.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.domain.model.types.ConnectingStatus
import npo.kib.odc_demo.feature_app.domain.model.types.SearchingStatus
import npo.kib.odc_demo.feature_app.data.p2p.P2PConnection
import java.io.InputStream
import java.util.*

@Suppress("BlockingMethodInNonBlockingContext")
class P2PConnectionBluetoothImpl(context: Context) : P2PConnection {
    private val _connectionResult: MutableStateFlow<ConnectingStatus> = MutableStateFlow(
        ConnectingStatus.NoConnection)
    override val connectionResult = _connectionResult.asStateFlow()
    private val _searchingStatusFlow: MutableStateFlow<SearchingStatus> = MutableStateFlow(
        SearchingStatus.NONE)
    override val searchingStatusFlow = _searchingStatusFlow.asStateFlow()
    private val _receivedBytes = MutableSharedFlow<ByteArray>()
    override val receivedBytes = _receivedBytes.asSharedFlow()

    private val scope = CoroutineScope(Dispatchers.IO)
    private val serviceUUID = UUID.fromString("133f71c6-b7b6-437e-8fd1-d2f59cc76066")
    private val adapter = BluetoothAdapter.getDefaultAdapter()
    private var connectedSocket: BluetoothSocket? = null

    override fun startDiscovery() {
        scope.launch {
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
        }
    }

    override fun stopDiscovery() {
        connectedSocket?.close()
        connectedSocket = null
        _searchingStatusFlow.update { SearchingStatus.NONE }
    }

    override fun send(bytes: ByteArray) {
        connectedSocket?.runCatching {
            val msg = (bytes.decodeToString() + "\n").encodeToByteArray()
            outputStream.write(msg)
            outputStream.flush()
        }
    }

    private suspend fun readInputStream(inputStream: InputStream) {
        val reader = inputStream.bufferedReader()

        runCatching {
            while (connectedSocket?.isConnected == true) {
                val line = reader.readLine()
                _receivedBytes.emit(line.encodeToByteArray())
            }
        }
    }

    override fun acceptConnection() = Unit

    override fun rejectConnection() = Unit
}