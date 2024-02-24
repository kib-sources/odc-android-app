package npo.kib.odc_demo.feature_app.domain.use_cases

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import npo.kib.odc_demo.feature_app.data.p2p.bluetooth.BluetoothState
import npo.kib.odc_demo.feature_app.domain.model.connection_status.BluetoothConnectionResult
import npo.kib.odc_demo.feature_app.domain.model.serialization.BytesToTypeConverter.deserializeToDataPacketVariant
import npo.kib.odc_demo.feature_app.domain.model.serialization.TypeToBytesConverter.serializeToByteArray
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.BluetoothController
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.CustomBluetoothDevice
import npo.kib.odc_demo.feature_app.domain.transaction_logic.SenderTransactionController

class P2PSendUseCaseNew(
    private val transactionController: SenderTransactionController,
    private val bluetoothController: BluetoothController,
    private val externalScope: CoroutineScope
) {

    private val packetsToSend = transactionController.outputDataPacketFlow
    private val transactionControllerInputChannel = transactionController.receivedPacketsChannel

    //    val connectionStatus: StateFlow<BluetoothConnectionStatus>
    val transactionDataBuffer = transactionController.transactionDataBuffer

    val bluetoothState = bluetoothController.bluetoothStateColdFlow.stateIn(
        externalScope, SharingStarted.WhileSubscribed(), BluetoothState()
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
                BluetoothConnectionResult.ConnectionEstablished -> transactionController.

                is BluetoothConnectionResult.TransferSucceeded -> transactionControllerInputChannel.send(
                    connectionResult.bytes.deserializeToDataPacketVariant()
                )

            }
        }.launchIn(externalScope)
    }

    fun startSendingPacketsFromTransactionController(): Boolean {
        return if (bluetoothState.value.isConnected) {
            packetsToSend.onEach { packet ->
                if (bluetoothState.value.isConnected) bluetoothController.trySendBytes(
                    packet.serializeToByteArray()
                )
                else throw Exception(
                    "Tried sending packets from transaction controller but no remote device is connected"
                )
            }.launchIn(externalScope)
            true
        } else false
    }


    suspend fun tryConstructAmount(amount : Int) : Boolean {
       return transactionController.tryConstructAmount(amount)
    }

    fun disconnect() {
        bluetoothController.closeConnection()
        transactionController.resetController()
    }

    fun reset() {
        externalScope.cancel("Cancelled scope in P2PSendUseCase")
        transactionController.resetController()
        bluetoothController.reset()
    }

}