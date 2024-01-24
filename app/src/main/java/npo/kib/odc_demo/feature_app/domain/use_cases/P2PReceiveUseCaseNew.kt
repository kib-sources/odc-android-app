package npo.kib.odc_demo.feature_app.domain.use_cases

import androidx.activity.result.ActivityResultRegistry
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import npo.kib.odc_demo.feature_app.domain.model.serialization.TypeToBytesConverter.serializeToByteArray
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.P2PConnectionBluetooth
import npo.kib.odc_demo.feature_app.domain.transaction_logic.ReceiverTransactionController
import javax.inject.Inject

class P2PReceiveUseCaseNew @Inject constructor(
    private val transactionController: ReceiverTransactionController,
    private val p2pConnection: P2PConnectionBluetooth
) {
    private val packetsToSend = transactionController.outputDataPacketFlow
    private val receivedBytes = p2pConnection.receivedBytes

    //    private val receivedData = p2pConnection/
    val connectionStatus = p2pConnection.connectionStatus

    lateinit var scope : CoroutineScope

    init {
        p2pConnection.scope = scope
    }
//    var scope : CoroutineScope
//        set(value) {
//            field = value
//            transactionController.scope = value
//            p2pConnection.scope = value
//        }
//        get() { return field}

//    fun setScope(scope: CoroutineScope){
//        scope = C
//    }

    private var currentJob: Job? = null

    fun startAdvertising(
        registry: ActivityResultRegistry, duration: Int, callback: (Int?) -> Unit
    ) {
        p2pConnection.startAdvertising(registry, duration) {
            callback(it)
            currentJob =
                scope.launch { p2pConnection.startBluetoothServerAndGetBytesFlow() }
        }
    }


    suspend fun startListeningForConnectionStatusUpdates(){
        scope.launch(Dispatchers.IO) {
            p2pConnection.connectionStatus.collect()
        }
    }


    suspend fun startSendingPacketsFromTransactionController() {
        packetsToSend.flowOn(scope.coroutineContext.job)
            .collect { packet -> p2pConnection.sendBytes(packet.serializeToByteArray()) }
    }

    fun stopAdvertising(registry: ActivityResultRegistry) {
        p2pConnection.stopAdvertising(registry)
    }

//    fun acceptConnection(){
//        p2pConnection.acceptConnection()
//    }
//
//    fun rejectConnection(){
//        p2pConnection.rejectConnection()
//    }

    //        private suspend fun addNewReceivedDataPacketVariant(bytes: ByteArray) {
//        val packetVariant = bytes.deserializeToDataPacketVariant()
//        _receivedData.send(packetVariant)
//
//    }
    suspend fun acceptOffer() {
        transactionController.sendOfferApproval()
    }

    suspend fun rejectOffer() {
        transactionController.sendOfferRejection()
    }

    fun reset() {
        currentJob?.cancel()
        currentJob = null
    }
}