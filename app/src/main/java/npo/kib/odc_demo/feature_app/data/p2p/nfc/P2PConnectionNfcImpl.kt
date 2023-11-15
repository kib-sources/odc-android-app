package npo.kib.odc_demo.feature_app.data.p2p.nfc

import android.content.Context
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import npo.kib.odc_demo.feature_app.domain.model.connection_status.BluetoothConnectionStatus
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnection

/** NFC is for interaction with the server only right now*/
class P2PConnectionNfcImpl(val context: Context) : P2PConnection {
    override val connectionStatus: StateFlow<BluetoothConnectionStatus>
        get() = TODO("Not yet implemented")
    override val receivedBytes: SharedFlow<ByteArray>
        get() = TODO("Not yet implemented")

    override fun startDiscovery() {
        TODO("Not yet implemented")
    }

    override fun stopDiscovery() {
        TODO("Not yet implemented")
    }

    override fun startAdvertising() {
        TODO("Not yet implemented")
    }

    override fun stopAdvertising() {
        TODO("Not yet implemented")
    }

    override suspend fun sendBytes(bytes: ByteArray): ByteArray? {
        TODO("Not yet implemented")
    }


    override fun acceptConnection() {
        TODO("Not yet implemented")
    }

    override fun rejectConnection() {
        TODO("Not yet implemented")
    }



    //    private val _connectionResult: MutableStateFlow<ConnectingStatus> =
//        MutableStateFlow(ConnectingStatus.NoConnection)
//    override val connectionStatus: StateFlow<ConnectingStatus> = _connectionResult.asStateFlow()
//    private val _searchingStatusFlow: MutableStateFlow<SearchingStatus> =
//        MutableStateFlow(SearchingStatus.NONE)
//    override val searchingStatusFlow: StateFlow<SearchingStatus> =
//        _searchingStatusFlow.asStateFlow()
//    private val _receivedBytes = MutableSharedFlow<ByteArray>()
//    override val receivedBytes: SharedFlow<ByteArray> = _receivedBytes.asSharedFlow()
//
//    private val scope = CoroutineScope(Dispatchers.IO)
//
//    private val intentFilter = IntentFilter(NfcServiceKeys.NFC_BROADCAST_FILTER)
//
//    private val receiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//
//            val isConnected = intent?.extras?.getSerializable(NfcServiceKeys.CONNECTED_KEY)
//            if (isConnected != null) {
//                myLogs(isConnected.toString())
//                if (isConnected == true) {
//                    _connectionResult.update {
//                        ConnectingStatus.ConnectionResult(
//                            ConnectionsStatusCodes.STATUS_OK
//                        )
//                    }
//                } else {
//                    _connectionResult.update { ConnectingStatus.Disconnected }
//                }
//            }
//
//            val bytes = intent?.extras?.getByteArray(NfcServiceKeys.RECEIVED_KEY)
//            if (bytes != null) {
//                myLogs("REC")
//                myLogs(bytes.decodeToString())
//                myLogs(bytes.toHex())
//                scope.launch {
//                    _receivedBytes.emit(bytes)
//                }
//            }
//        }
//    }
//
//    override fun startDiscovery() {
//        val serviceIntent =
//            Intent(context, NfcService::class.java).putExtra(
//                NfcServiceKeys.SERVICE_ENABLE_KEY,
//                NfcServiceCommands.ENABLE
//            )
//        context.startService(serviceIntent)
//        context.registerReceiver(receiver, intentFilter)
//        _searchingStatusFlow.update { SearchingStatus.DISCOVERING }
//    }
//
//    override fun stopDiscovery() {
//        val serviceIntent =
//            Intent(context, NfcService::class.java).putExtra(
//                NfcServiceKeys.SERVICE_ENABLE_KEY,
//                NfcServiceCommands.DISABLE
//            )
//        context.startService(serviceIntent)
//        context.unregisterReceiver(receiver)
//        _searchingStatusFlow.update { SearchingStatus.NONE }
//        _connectionResult.update { ConnectingStatus.Disconnected }
//    }
//
//    override fun sendBytes(bytes: ByteArray) {
//        myLogs("SENDING")
//        myLogs(bytes.decodeToString())
//        val serviceIntent =
//            Intent(context, NfcService::class.java).putExtra(NfcServiceKeys.SEND_KEY, bytes)
//        context.startService(serviceIntent)
//    }
//
//    override fun acceptConnection() = Unit
//
//    override fun rejectConnection() = Unit
//
//    override fun connectToDevice() = Unit
}