package npo.kib.odc_demo.feature_app.domain.use_cases

import androidx.activity.result.ActivityResultRegistry
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import npo.kib.odc_demo.feature_app.data.p2p.bluetooth.BluetoothState
import npo.kib.odc_demo.feature_app.domain.model.connection_status.BluetoothConnectionResult
import npo.kib.odc_demo.feature_app.domain.model.serialization.BytesToTypeConverter.deserializeToDataPacketVariant
import npo.kib.odc_demo.feature_app.domain.model.serialization.TypeToBytesConverter.serializeToByteArray
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.UserInfo
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.BluetoothController
import npo.kib.odc_demo.feature_app.domain.transaction_logic.ReceiverTransactionController
import npo.kib.odc_demo.feature_app.domain.util.cancelChildren

class P2PReceiveUseCaseNew(
    private val transactionController: ReceiverTransactionController,
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

    fun startAdvertising(
        registry: ActivityResultRegistry, duration: Int, callback: (Int?) -> Unit
    ) {
        bluetoothController.startAdvertising(registry, duration) {
            callback(it)
            it?.let {
                startBluetoothServerAndRoutePacketsToTransactionController()
            }
        }
    }

    fun stopAdvertising(registry: ActivityResultRegistry) =
        bluetoothController.stopAdvertising(registry)

    private fun startBluetoothServerAndRoutePacketsToTransactionController() {
        bluetoothController.startBluetoothServerAndGetFlow().onEach { connectionResult ->
            when (connectionResult) {
                is BluetoothConnectionResult.ConnectionEstablished -> {
                    transactionController.initController()
                    startSendingPacketsFromTransactionController()
                    transactionController.startProcessingIncomingPackets()
                }
                is BluetoothConnectionResult.TransferSucceeded -> transactionControllerInputChannel.send(
                    connectionResult.bytes.deserializeToDataPacketVariant()
                )
//                else -> {}
            }
        }.onCompletion { withContext(NonCancellable) { transactionController.resetController() } }
            .launchIn(scope)
    }

    private fun startSendingPacketsFromTransactionController(): Boolean {
        return if (bluetoothState.value.isConnected) {
            packetsToSend.onEach { packet ->
                if (bluetoothState.value.isConnected) bluetoothController.trySendBytes(
                    packet.serializeToByteArray()
                )
                else throw Exception(
                    "Tried sending packets from transaction controller but no remote device is connected"
                )
            }.flowOn(Dispatchers.IO).launchIn(scope)
            true
        } else false
    }

    fun acceptOffer() {
        scope.launch {
            transactionController.sendAmountRequestApproval()
        }
    }

    suspend fun rejectOffer() {
        scope.launch {
            transactionController.sendAmountRequestRejection()
        }
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

    fun updateLocalUserInfo(userInfo: UserInfo) =
        transactionController.updateLocalUserInfo(userInfo)

    fun sendUserInfo(userInfo: UserInfo) {
        scope.launch { transactionController.sendUserInfo(userInfo) }
    }
}