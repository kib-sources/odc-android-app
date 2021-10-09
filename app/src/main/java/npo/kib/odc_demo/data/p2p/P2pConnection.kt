package npo.kib.odc_demo.data.p2p

import com.google.android.gms.nearby.connection.Payload
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import npo.kib.odc_demo.data.models.ConnectingStatus
import npo.kib.odc_demo.data.models.SearchingStatus

// Базоый интерфейс для p2p соеденений
interface P2pConnection {
    val connectionResult: StateFlow<ConnectingStatus>
    val searchingStatusFlow: StateFlow<SearchingStatus>
    val receivedBytes: SharedFlow<ByteArray>
    fun startDiscovery()
    fun stopDiscovery()
    fun send(bytes: ByteArray)
    fun onReceive(endpointId: String, payload: Payload)
    fun acceptConnection()
    fun rejectConnection()
}