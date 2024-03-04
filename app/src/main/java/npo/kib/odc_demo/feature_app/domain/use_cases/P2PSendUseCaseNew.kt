package npo.kib.odc_demo.feature_app.domain.use_cases

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import npo.kib.odc_demo.feature_app.data.p2p.bluetooth.BluetoothConnectionStatus
import npo.kib.odc_demo.feature_app.data.p2p.bluetooth.BluetoothState
import npo.kib.odc_demo.feature_app.domain.model.connection_status.BluetoothConnectionResult
import npo.kib.odc_demo.feature_app.domain.model.serialization.BytesToTypeConverter.deserializeToDataPacketVariant
import npo.kib.odc_demo.feature_app.domain.model.serialization.TypeToBytesConverter.serializeToByteArray
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.BluetoothController
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.CustomBluetoothDevice
import npo.kib.odc_demo.feature_app.domain.transaction_logic.SenderTransactionController
import npo.kib.odc_demo.feature_app.domain.util.cancelChildren

class P2PSendUseCaseNew(
    private val transactionController: SenderTransactionController,
    private val bluetoothController: BluetoothController,
    private val scope: CoroutineScope
) {
    private var connectionJob: Job? = null

    private val packetsToSend = transactionController.outputDataPacketFlow
    private val transactionControllerInputChannel = transactionController.receivedPacketsChannel

    val transactionDataBuffer = transactionController.transactionDataBuffer

    val transactionStatus = transactionController.transactionStatus

    val bluetoothState = bluetoothController.bluetoothStateColdFlow.stateIn(
        scope,
        SharingStarted.WhileSubscribed(replayExpirationMillis = 0),
        BluetoothState()
    )

    val blErrors = bluetoothController.errors
    val transactionErrors = transactionController.errors

    fun startDiscovery() {
        bluetoothController.startDiscovery()
    }

    fun stopDiscovery() {
        bluetoothController.stopDiscovery()
    }


    fun connectToDevice(device: CustomBluetoothDevice) {
        cancelJob()
        connectionJob = bluetoothController.connectToDevice(device).onEach { connectionResult ->
            when (connectionResult) {
                BluetoothConnectionResult.ConnectionEstablished -> {
                    transactionController.initController()
                    startSendingPacketsFromTransactionController()
                    //todo handle the situation when an exception happens and the flow in this method
                    // is cancelled. Maybe send an ERROR packet or 10 TransactionResult failure packets...
                    // for now we should be staying connected with bluetooth but on the ERROR screen
                    // and be able to disconnect by pressing the "disconnect" UI button.
                    transactionController.startProcessingIncomingPackets()
                }
                is BluetoothConnectionResult.TransferSucceeded -> transactionControllerInputChannel.send(
                    connectionResult.bytes.deserializeToDataPacketVariant()
                )
            }
        }.onCompletion {
            withContext(
                NonCancellable
            ) { transactionController.resetController() }
        }.launchIn(scope)
    }

    private fun startSendingPacketsFromTransactionController(): Boolean {
        return if (bluetoothState.value.connectionStatus == BluetoothConnectionStatus.CONNECTED) {
            packetsToSend.onEach { packet ->
                if (bluetoothState.value.connectionStatus == BluetoothConnectionStatus.CONNECTED) bluetoothController.trySendBytes(
                    packet.serializeToByteArray()
                )
//                else throw Exception(
//                    "Tried sending packets from transaction controller but no remote device is connected"
//                )
            }.launchIn(scope)
            true
        } else false
    }

    fun updateLocalUserInfo() = transactionController.updateLocalUserInfo()

    /** Is main-safe. */
    suspend fun tryConstructAmount(amount: Int) {
       transactionController.tryConstructAmount(amount)
    }

    /**
     *  Can call only after a successful tryConstructAmount()
     * */
    fun trySendOffer() {
        scope.launch { transactionController.trySendOffer() }
    }

    fun disconnect() {
        bluetoothController.closeConnection()
        cancelJob()
//      transactionController.resetController() is invoked automatically in onCompletion{} for bl packets flow
    }

    fun reset() {
        scope.cancelChildren()
        cancelJob()
        transactionController.resetController()
        bluetoothController.reset()
    }

    private fun cancelJob() {
        connectionJob?.cancel()
        connectionJob = null
    }

}