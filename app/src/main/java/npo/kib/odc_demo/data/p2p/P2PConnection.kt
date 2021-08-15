package npo.kib.odc_demo.data.p2p

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import npo.kib.odc_demo.data.models.ConnectingStatus

class P2PConnection(context: Context) {
    private val mConnectionsClient = Nearby.getConnectionsClient(context)
    private val serviceId = "npo.kib.odc_demo"
    private val userName = "User"
    private var mIsAdvertising = false
    private var mIsDiscovering = false
    private lateinit var connectionEndpoint: String
    private val _connectionResult: MutableStateFlow<ConnectingStatus> =
        MutableStateFlow(ConnectingStatus.NoConnection)
    val connectionResult = _connectionResult.asStateFlow()
    private val _receivedBytes = MutableSharedFlow<ByteArray>()
    val receivedBytes = _receivedBytes.asSharedFlow()

    fun startAdvertising() {
        val advertisingOptions =
            AdvertisingOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        mConnectionsClient
            .startAdvertising(
                userName, serviceId, connectionLifecycleCallback, advertisingOptions
            )
            .addOnSuccessListener {
                mIsAdvertising = true
                // We're advertising!
                Log.d("OpenDigitalCash", "We're advertising!")
            }
            .addOnFailureListener {
                mIsAdvertising = false
                // We were unable to start advertising.
                Log.d("OpenDigitalCash", "We were unable to start advertising.")
            }
    }

    fun startDiscovery() {
        val discoveryOptions =
            DiscoveryOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        mConnectionsClient
            .startDiscovery(serviceId, endpointDiscoveryCallback, discoveryOptions)
            .addOnSuccessListener {
                mIsDiscovering = true
                Log.d("OpenDigitalCash", "We're discovering!")
            }
            .addOnFailureListener { e ->
                mIsDiscovering = false
                Log.d("OpenDigitalCash", "We were unable to start discovering.")
            }
    }

    private fun stopAdvertising() {
        mIsAdvertising = false
        mConnectionsClient.stopAdvertising()
    }

    private fun stopDiscovering() {
        mIsDiscovering = false
        mConnectionsClient.stopDiscovery()
    }

    private val endpointDiscoveryCallback =
        object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                // An endpoint was found. We request a connection to it.
                Log.d("OpenDigitalCash", "An endpoint was found. We request a connection to it.")
                mConnectionsClient
                    .requestConnection(userName, endpointId, connectionLifecycleCallback)
                    .addOnSuccessListener {
                        // We successfully requested a connection. Now both sides
                        // must accept before the connection is established.
                    }
                    .addOnFailureListener {
                        // Nearby Connections failed to request the connection.})
                    }
            }

            override fun onEndpointLost(endpointId: String) {
                // A previously discovered endpoint has gone away.
            }
        }

    private val connectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
                Log.d("OpenDigitalCash", "onConnectionInitiated")
                //TODO добавить диалог подтверждения соединения
                connectionEndpoint = endpointId
                _connectionResult.update { ConnectingStatus.ConnectionInitiated(info) }
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                _connectionResult.update { ConnectingStatus.ConnectionResult(result) }
            }

            override fun onDisconnected(endpointId: String) {
                // We've been disconnected from this endpoint. No more data can be
                // sent or received.
                _connectionResult.update { ConnectingStatus.Disconnected }
            }
        }


    fun send(bytes: ByteArray) {
        val payload = Payload.fromBytes(bytes)
        mConnectionsClient
            .sendPayload(connectionEndpoint, payload)
            .addOnFailureListener { e -> Log.d("sendPayload() failed.", e.toString()) }
    }

    private val mPayloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            Log.d("onPayloadReceived", "(endpointId=$endpointId, payload=$payload")
            onReceive(endpointId, payload)
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            Log.d("onPayloadTransferUpdate", "endpointId=$endpointId, update=$update")
        }
    }

    fun onReceive(endpointId: String, payload: Payload) {
        Log.d("ODCRec", "(endpointId=$endpointId, payload=$payload")
        if (payload.type == Payload.Type.BYTES && endpointId == connectionEndpoint) {
            CoroutineScope(Dispatchers.IO).launch {
                payload.asBytes()?.let { _receivedBytes.emit(it) }
            }
        }
    }

    fun acceptConnection() {
        mConnectionsClient.acceptConnection(connectionEndpoint, mPayloadCallback)
    }

    fun rejectConnection() {
        mConnectionsClient.rejectConnection(connectionEndpoint)
    }
}