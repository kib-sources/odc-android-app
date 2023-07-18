package npo.kib.odc_demo.data.p2p

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import npo.kib.odc_demo.data.models.ConnectingStatus
import npo.kib.odc_demo.data.models.SearchingStatus

// Базовый интерфейс для p2p соеденений
interface P2pConnection {
    val connectionResult: StateFlow<ConnectingStatus>
    val searchingStatusFlow: StateFlow<SearchingStatus>
    val receivedBytes: SharedFlow<ByteArray>
    fun startDiscovery()
    fun stopDiscovery()
    fun send(bytes: ByteArray)
    fun acceptConnection()
    fun rejectConnection()
}