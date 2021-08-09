package npo.kib.odc_demo.data

import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class P2PConnection(context: Context) {
    private val mConnectionsClient = Nearby.getConnectionsClient(context)
    private val serviceId = "npo.kib.odc_demo"
    private val userName = "User"
    private var mIsAdvertising = false
    private var mIsDiscovering = false
    private lateinit var mConnection: String
    private var mIsConnecting = false
    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()

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
//                AlertDialog.Builder(context)
//                    .setTitle("Accept connection to " + info.endpointName)
//                    .setMessage("Confirm the code matches on both devices: " + info.authenticationDigits)
//                    .setPositiveButton(
//                        "Accept"
//                    ) { dialog: DialogInterface?, which: Int ->  // The user confirmed, so we can accept the connection.
//                        mConnection = endpointId
//                        mConnectionsClient
//                            .acceptConnection(endpointId, mPayloadCallback)
//                    }
//                    .setNegativeButton(
//                        R.string.cancel
//                    ) { dialog: DialogInterface?, which: Int ->  // The user canceled, so we should reject the connection.
//                        mConnectionsClient.rejectConnection(endpointId)
//                    }
//                    .setIcon(R.drawable.ic_dialog_alert)
//
//                    .show()
                mConnection = endpointId
                mConnectionsClient
                    .acceptConnection(endpointId, mPayloadCallback)
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        // We're connected! Can now start sending and receiving data.
                        if (mIsAdvertising) {
                            _isConnected.value = true
                        }
                        mIsConnecting = true
                        Log.d("OpenDigitalCashP", "status ok")
                    }
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                        // The connection was rejected by one or both sides.
                    }
                    ConnectionsStatusCodes.STATUS_ERROR -> {
                        // The connection broke before it was able to be accepted.
                    }
                    else -> {
                        // Unknown status code
                    }
                }
            }

            override fun onDisconnected(endpointId: String) {
                // We've been disconnected from this endpoint. No more data can be
                // sent or received.
            }
        }


    fun send(bytes: ByteArray) {
        val payload = Payload.fromBytes(bytes)
        mConnectionsClient
            .sendPayload(mConnection, payload)
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
    }
}