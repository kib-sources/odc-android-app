package npo.kib.odc_demo.feature_app.domain.use_cases

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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

    private val packetsToSend = transactionController.outputDataPacketFlow
    private val transactionControllerInputChannel = transactionController.receivedPacketsChannel

    val transactionDataBuffer = transactionController.transactionDataBuffer

    val currentTransactionStep = transactionController.currentStep

    val bluetoothState = bluetoothController.bluetoothStateColdFlow.stateIn(
        scope, SharingStarted.WhileSubscribed(), BluetoothState()
    )

    val errors = bluetoothController.errors

    fun startDiscovery() {
        bluetoothController.startDiscovery()
    }

    fun stopDiscovery() {
        bluetoothController.stopDiscovery()
    }

    fun connectToDevice(device: CustomBluetoothDevice) {
        bluetoothController.connectToDevice(device).onEach { connectionResult ->
            when (connectionResult) {
                BluetoothConnectionResult.ConnectionEstablished -> {
                    transactionController.initController()
                    startSendingPacketsFromTransactionController()
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
        return if (bluetoothState.value.isConnected) {
            packetsToSend.onEach { packet ->
                if (bluetoothState.value.isConnected) bluetoothController.trySendBytes(
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

    suspend fun tryConstructAmount(amount: Int): Boolean {
        return transactionController.tryConstructAmount(amount)
    }

    /**
     *  Can call only after a successful tryConstructAmount()
     * */
    suspend fun trySendOffer(){
        transactionController.trySendOffer()
    }

    fun disconnect() {
        bluetoothController.closeConnection()
        //invoked automatically in onCompletion{} for bl packets flow
//        transactionController.resetController()
    }

    fun reset() {
        scope.cancelChildren()
        transactionController.resetController()
        bluetoothController.reset()
    }

}