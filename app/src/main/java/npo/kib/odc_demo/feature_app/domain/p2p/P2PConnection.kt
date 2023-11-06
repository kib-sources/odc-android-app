package npo.kib.odc_demo.feature_app.domain.p2p

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import npo.kib.odc_demo.feature_app.domain.model.connection_status.ConnectingStatus
import npo.kib.odc_demo.feature_app.domain.model.connection_status.SearchingStatus

interface P2PConnection {
    val connectionResult: StateFlow<ConnectingStatus>
    val searchingStatusFlow: StateFlow<SearchingStatus>
    val receivedBytes: SharedFlow<ByteArray>
    fun startDiscovery()
    fun stopDiscovery()
    fun sendBytes(bytes: ByteArray)
    fun acceptConnection()
    fun rejectConnection()
    fun connectToDevice()
}