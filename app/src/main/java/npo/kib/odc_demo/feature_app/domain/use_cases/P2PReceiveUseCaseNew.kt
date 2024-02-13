package npo.kib.odc_demo.feature_app.domain.use_cases

import androidx.activity.result.ActivityResultRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.data.p2p.bluetooth.BluetoothState
import npo.kib.odc_demo.feature_app.domain.model.connection_status.BluetoothConnectionResult
import npo.kib.odc_demo.feature_app.domain.model.serialization.BytesToTypeConverter.deserializeToDataPacketVariant
import npo.kib.odc_demo.feature_app.domain.model.serialization.TypeToBytesConverter.serializeToByteArray
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.UserInfo
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.BluetoothController
import npo.kib.odc_demo.feature_app.domain.transaction_logic.ReceiverTransactionController

class P2PReceiveUseCaseNew(
    private val transactionController: ReceiverTransactionController,
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

    fun startAdvertising(
        registry: ActivityResultRegistry, duration: Int, callback: (Int?) -> Unit
    ) {
        bluetoothController.startAdvertising(registry, duration) {
            callback(it)
            startBluetoothServerAndRoutePacketsToTransactionController()
            with(transactionController) {
                initController()
                startProcessingIncomingPackets()
            }
        }
    }

    fun stopAdvertising(registry: ActivityResultRegistry) =
        bluetoothController.stopAdvertising(registry)


    private fun startBluetoothServerAndRoutePacketsToTransactionController() {
        bluetoothController.startBluetoothServerAndGetFlow().onEach { connectionResult ->
            when (connectionResult) {
//                is BluetoothConnectionResult.ConnectionEstablished -> {}
                is BluetoothConnectionResult.TransferSucceeded -> transactionControllerInputChannel.send(
                    connectionResult.bytes.deserializeToDataPacketVariant()
                )

//                else -> {}
            }
        }.launchIn(externalScope)
    }


    fun startSendingPacketsFromTransactionController() {
        externalScope.launch {
            if (bluetoothState.value.isConnected) packetsToSend.flowOn(Dispatchers.IO)
                .collect { packet ->
                    if (bluetoothState.value.isConnected) bluetoothController.trySendBytes(
                        packet.serializeToByteArray()
                    )
                }
            else throw Exception("Tried sending packets from transaction controller but no remote device is connected")
        }
    }

    fun acceptOffer() {
        externalScope.launch {
            transactionController.sendOfferApproval()
        }
    }

    suspend fun rejectOffer() {
        externalScope.launch {
            transactionController.sendOfferRejection()
        }
    }

    fun disconnect() {
        bluetoothController.closeConnection()
        transactionController.resetTransactionController()
    }

    fun reset() {
        externalScope.cancel("Cancelled scope in P2PReceiveUseCase")
        transactionController.resetTransactionController()
        bluetoothController.reset()
    }

    fun updateLocalUserInfo(userInfo: UserInfo) =
        transactionController.updateLocalUserInfo(userInfo)

    fun sendUserInfo(userInfo: UserInfo) {
        externalScope.launch { transactionController.sendUserInfo(userInfo) }
    }


}