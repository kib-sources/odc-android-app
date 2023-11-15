package npo.kib.odc_demo.feature_app.domain.p2p

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import npo.kib.odc_demo.feature_app.domain.model.connection_status.BluetoothConnectionStatus

interface P2PConnection {
    val connectionStatus: StateFlow<BluetoothConnectionStatus>
    val receivedBytes: SharedFlow<ByteArray>

    fun startDiscovery()
    fun stopDiscovery()
    
    fun startAdvertising()
    fun stopAdvertising()

    //returns null if dataTransferService is not online
    suspend fun sendBytes(bytes: ByteArray): ByteArray?

    fun acceptConnection()
    fun rejectConnection()

}