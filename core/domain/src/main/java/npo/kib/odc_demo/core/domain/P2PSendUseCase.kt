package npo.kib.odc_demo.core.domain

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import npo.kib.odc_demo.core.common.data.di.P2PUseCaseScope
import npo.kib.odc_demo.core.common.data.util.log
import npo.kib.odc_demo.core.common.data.util.logOut
import npo.kib.odc_demo.core.common_jvm.cancelChildren
import npo.kib.odc_demo.core.connectivity.bluetooth.BluetoothConnectionResult.ConnectionEstablished
import npo.kib.odc_demo.core.connectivity.bluetooth.BluetoothConnectionResult.TransferSucceeded
import npo.kib.odc_demo.core.connectivity.bluetooth.BluetoothConnectionStatus
import npo.kib.odc_demo.core.connectivity.bluetooth.BluetoothController
import npo.kib.odc_demo.core.connectivity.bluetooth.BluetoothState
import npo.kib.odc_demo.core.model.CustomBluetoothDevice
import npo.kib.odc_demo.core.transaction_logic.SenderTransactionController
import npo.kib.odc_demo.core.wallet.model.serialization.BytesToTypeConverter.deserializeToDataPacketVariant
import npo.kib.odc_demo.core.wallet.model.serialization.TypeToBytesConverter.toSerializedDataPacket
import javax.inject.Inject

//@ViewModelScoped
class P2PSendUseCase @Inject constructor(
    private val transactionController: SenderTransactionController,
    private val bluetoothController: BluetoothController,
    @P2PUseCaseScope private val scope: CoroutineScope
) {
    private var connectionJob: Job? = null

    private val packetsToSend = transactionController.outputDataPacketFlow
    private val transactionControllerInputChannel = transactionController.receivedPacketsChannel

    val transactionDataBuffer = transactionController.transactionDataBuffer

    val transactionStatus = transactionController.transactionStatus

    val bluetoothState = bluetoothController.bluetoothStateColdFlow.stateIn(
        scope, SharingStarted.WhileSubscribed(replayExpirationMillis = 0), BluetoothState()
    )

    private val _useCaseErrors = MutableSharedFlow<String>(extraBufferCapacity = 5)
    val useCaseErrors: SharedFlow<String> = _useCaseErrors.asSharedFlow()

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
                ConnectionEstablished -> {
                    transactionController.initController()
                    startSendingPacketsFromTransactionController()
                    //todo handle the situation when an exception happens and the flow in this method
                    // is cancelled. Maybe send an ERROR packet or 10 TransactionResult failure packets...
                    // for now we should be staying connected with bluetooth but on the ERROR screen
                    // and be able to disconnect by pressing the "disconnect" UI button.
                    transactionController.startProcessingIncomingPackets()
                }

                is TransferSucceeded -> transactionControllerInputChannel.send(
                    connectionResult.bytes.deserializeToDataPacketVariant()
                        .logOut("Received DataPacketVariant:\n", tag = "P2PSendUseCase")
                )
            }
        }.onCompletion {
            withContext(NonCancellable) { transactionController.resetController() }
        }.launchIn(scope)
    }

    private fun startSendingPacketsFromTransactionController(): Boolean {
        return if (bluetoothState.value.connectionStatus == BluetoothConnectionStatus.CONNECTED) {
            packetsToSend.onEach { packet ->
                this@P2PSendUseCase.log("BLUETOOTH conn status = ${bluetoothState.value.connectionStatus}\nGoing to send packet: ${packet.packetType}" )
                if (bluetoothState.value.connectionStatus == BluetoothConnectionStatus.CONNECTED) bluetoothController.trySendBytes(
                    packet.toSerializedDataPacket()
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
        if (amount > 0) transactionController.tryConstructAmount(amount)
        else _useCaseErrors.emit("The amount must be positive")
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