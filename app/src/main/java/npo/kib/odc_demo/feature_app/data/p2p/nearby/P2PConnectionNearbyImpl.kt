package npo.kib.odc_demo.feature_app.data.p2p.nearby

import android.content.Context
import com.google.android.gms.nearby.Nearby
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import npo.kib.odc_demo.feature_app.domain.model.connection_status.BluetoothConnectionStatus
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnection

// Имплементация расширенного интерфейс p2p соеденений на базе Google Nearby Connections API
class P2PConnectionNearbyImpl(context: Context) : P2PConnection {

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
//
//    override suspend fun sendBytes(bytes: ByteArray) {
//        TODO("Not yet implemented")
//    }

    override fun acceptConnection() {
        TODO("Not yet implemented")
    }

    override fun rejectConnection() {
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

    private val mConnectionsClient = Nearby.getConnectionsClient(context)
//    private val serviceId = context.resources.getString(R.string.app_package)
    private lateinit var connectionEndpoint: String
//
//    private val usernameKey = context.resources.getString(R.string.username_key)
//    private val defaultUsername = "User"
//    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
//    private val userName = prefs.getString(usernameKey, defaultUsername) ?: defaultUsername
//
//    private val _connectionResult = MutableStateFlow<ConnectingStatus>(ConnectingStatus.NoConnection)
//    override val connectionStatus = _connectionResult.asStateFlow()
//
//    private val _searchingStatusFlow = MutableStateFlow(SearchingStatus.NONE)
//    override val searchingStatusFlow = _searchingStatusFlow.asStateFlow()
//
//    private val _receivedBytes = MutableSharedFlow<ByteArray>()
//    override val receivedBytes = _receivedBytes.asSharedFlow()
//
//    override fun startAdvertising() {
//        val advertisingOptions = AdvertisingOptions.Builder()
//            .setStrategy(Strategy.P2P_POINT_TO_POINT)
//            .build()
//
//        mConnectionsClient
//            .startAdvertising(userName, serviceId, connectionLifecycleCallback, advertisingOptions)
//            .addOnSuccessListener {
//                _searchingStatusFlow.update { SearchingStatus.ADVERTISING }
//            }.addOnFailureListener {
//                val exception = it as ApiException
//                myLogs(exception.status)
//                _searchingStatusFlow.update { SearchingStatus.FAILURE }
//            }
//    }
//
//    override fun startDiscovery() {
//        val discoveryOptions = DiscoveryOptions.Builder()
//            .setStrategy(Strategy.P2P_POINT_TO_POINT)
//            .build()
//
//        mConnectionsClient
//            .startDiscovery(serviceId, endpointDiscoveryCallback, discoveryOptions)
//            .addOnSuccessListener {
//                _searchingStatusFlow.update { SearchingStatus.DISCOVERING }
//            }
//            .addOnFailureListener {
//                _searchingStatusFlow.update { SearchingStatus.FAILURE }
//            }
//    }
//
//    override fun stopAdvertising() {
//        mConnectionsClient.stopAllEndpoints()
//        mConnectionsClient.stopAdvertising()
//        _searchingStatusFlow.update { SearchingStatus.NONE }
//    }
//
//    override fun stopDiscovery() {
//        mConnectionsClient.stopAllEndpoints()
//        mConnectionsClient.stopDiscovery()
//        _searchingStatusFlow.update { SearchingStatus.NONE }
//    }
//
//    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
//        // An endpoint was found. We request a connection to it.
//        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
//            mConnectionsClient
//                .requestConnection(userName, endpointId, connectionLifecycleCallback)
//                .addOnSuccessListener {
//                    // We successfully requested a connection. Now both sides
//                    // must accept before the connection is established.
//                }
//                .addOnFailureListener {
//                    // Nearby Connections failed to request the connection.})
//                }
//        }
//
//        // A previously discovered endpoint has gone away.
//        override fun onEndpointLost(endpointId: String) = Unit
//    }
//
//    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
//        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
//            connectionEndpoint = endpointId
//            _connectionResult.update { ConnectingStatus.ConnectionInitiated(info) }
//        }
//
//        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
//            _connectionResult.update { ConnectingStatus.ConnectionResult(result.status.statusCode) }
//        }
//
//        override fun onDisconnected(endpointId: String) {
//            // We've been disconnected from this endpoint. No more data can be
//            // sent or received.
//            _connectionResult.update { ConnectingStatus.Disconnected }
//        }
//    }
//
//
//    override suspend fun sendBytes(bytes: ByteArray) {
//        val payload = Payload.fromBytes(bytes)
//        mConnectionsClient
//            .sendPayload(connectionEndpoint, payload)
//            .addOnFailureListener { e -> Log.d("sendPayload() failed.", e.toString()) }
//    }
//
//    private val mPayloadCallback: PayloadCallback = object : PayloadCallback() {
//        override fun onPayloadReceived(endpointId: String, payload: Payload) {
//            onReceive(endpointId, payload)
//        }
//
//        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {}
//    }
//
//    private fun onReceive(endpointId: String, payload: Payload) {
//        if (payload.type == Payload.Type.BYTES && endpointId == connectionEndpoint) {
//            CoroutineScope(Dispatchers.IO).launch {
//                payload.asBytes()?.let { _receivedBytes.emit(it) }
//            }
//        }
//    }
//
//    override fun acceptConnection() {
//        mConnectionsClient.acceptConnection(connectionEndpoint, mPayloadCallback)
//    }
//
//    override fun rejectConnection() {
//        mConnectionsClient.rejectConnection(connectionEndpoint)
//    }
//
//    override fun connectToDevice() {
//        TODO("Not yet implemented")
//    }
}