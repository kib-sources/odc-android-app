package npo.kib.odc_demo.data.p2p

import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import npo.kib.odc_demo.data.models.ConnectingStatus
import npo.kib.odc_demo.data.models.SearchingStatus
import java.net.ServerSocket
import java.net.Socket

@Suppress("BlockingMethodInNonBlockingContext")
class P2pConnectionBidirectionalTcpImpl(private val ip: String = "192.168.1.19") : P2pConnectionBidirectional {
    private val _connectionResult: MutableStateFlow<ConnectingStatus> = MutableStateFlow(ConnectingStatus.NoConnection)
    override val connectionResult = _connectionResult.asStateFlow()
    private val _searchingStatusFlow: MutableStateFlow<SearchingStatus> = MutableStateFlow(SearchingStatus.NONE)
    override val searchingStatusFlow = _searchingStatusFlow.asStateFlow()
    private val _receivedBytes = MutableSharedFlow<ByteArray>()
    override val receivedBytes = _receivedBytes.asSharedFlow()

    private val scope = CoroutineScope(Dispatchers.IO)
    private var _serverSocket: ServerSocket? = null
    private var _clientSocket: Socket? = null

    override fun startAdvertising() {
        scope.launch {
            val serverSocket = ServerSocket(14900)
            _serverSocket = serverSocket
            _searchingStatusFlow.update { SearchingStatus.ADVERTISING }

            val client = serverSocket.accept()
            _clientSocket = client
            _connectionResult.update { ConnectingStatus.ConnectionResult(ConnectionsStatusCodes.STATUS_OK) }
            readSocket(client)
        }
    }

    override fun stopAdvertising() {
        _serverSocket?.close()
        _serverSocket = null
        _searchingStatusFlow.update { SearchingStatus.NONE }
    }

    override fun startDiscovery() {
        scope.launch {
            val socket = Socket(ip, 14900)
            _clientSocket = socket
            _connectionResult.update { ConnectingStatus.ConnectionResult(ConnectionsStatusCodes.STATUS_OK) }
            readSocket(socket)
        }
    }

    override fun stopDiscovery() {
        TODO("Not yet implemented")
    }

    override fun send(bytes: ByteArray) {
        _clientSocket?.runCatching {
            val outputStream = getOutputStream()
            outputStream.write(bytes)
            outputStream.flush()
        }
    }

    private suspend fun readSocket(socket: Socket) {
        val inputStream = socket.getInputStream().bufferedReader()

        while (socket.isConnected) {
            val data = CharArray(1024 * 4)
            val count = inputStream.read(data, 0, data.size)
            if (count == -1) {
                socket.close()
                _clientSocket = null
                _connectionResult.update { ConnectingStatus.Disconnected }
                break
            }

            if (count == 0) continue

            val msg = String(data, 0, count)
            _receivedBytes.emit(msg.encodeToByteArray())
        }
    }

    override fun acceptConnection() = Unit

    override fun rejectConnection() = Unit
}