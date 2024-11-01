package npo.kib.odc_demo.core.connectivity.nfc

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import npo.kib.odc_demo.core.model.ConnectingStatus
import npo.kib.odc_demo.core.model.SearchingStatus

interface NfcController {


    val connectionResult: StateFlow<ConnectingStatus>
    val searchingStatusFlow: StateFlow<SearchingStatus>
    val receivedBytes: SharedFlow<ByteArray>
    fun startDiscovery()
    fun stopDiscovery()
    fun send(bytes: ByteArray)
    fun acceptConnection()
    fun rejectConnection()
}