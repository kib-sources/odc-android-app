package npo.kib.odc_demo.core.domain

import androidx.activity.result.ActivityResultRegistry
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import npo.kib.odc_demo.core.common.data.util.log
import npo.kib.odc_demo.core.common.data.util.logOut
import npo.kib.odc_demo.core.connectivity.bluetooth.BluetoothController
import npo.kib.odc_demo.core.datastore.DefaultDataStoreObject.USER_NAME
import npo.kib.odc_demo.core.datastore.DefaultDataStoreRepository
import npo.kib.odc_demo.core.datastore.UtilityDataStoreObject.CACHED_BLUETOOTH_NAME
import npo.kib.odc_demo.core.datastore.UtilityDataStoreObject.IS_BLUETOOTH_NAME_CHANGED
import npo.kib.odc_demo.core.datastore.UtilityDataStoreRepository
import npo.kib.odc_demo.core.connectivity.bluetooth.BluetoothConnectionStatus
import npo.kib.odc_demo.core.connectivity.bluetooth.BluetoothState
import npo.kib.odc_demo.core.common.data.di.P2PUseCaseScope
import npo.kib.odc_demo.core.common_jvm.cancelChildren
import npo.kib.odc_demo.core.connectivity.bluetooth.BluetoothConnectionResult.ConnectionEstablished
import npo.kib.odc_demo.core.connectivity.bluetooth.BluetoothConnectionResult.TransferSucceeded
import npo.kib.odc_demo.core.transaction_logic.ReceiverTransactionController
import npo.kib.odc_demo.core.wallet.model.serialization.BytesToTypeConverter.deserializeToDataPacketVariant
import npo.kib.odc_demo.core.wallet.model.serialization.TypeToBytesConverter.toSerializedDataPacket
import javax.inject.Inject

//@ViewModelScoped
class P2PReceiveUseCase @Inject constructor(
    private val transactionController: ReceiverTransactionController,
    private val bluetoothController: BluetoothController,
    private val defaultDataStoreRepository: DefaultDataStoreRepository,
    private val utilityDataStoreRepository: UtilityDataStoreRepository,
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

    fun startAdvertising(
        registry: ActivityResultRegistry, duration: Int, callback: (Int?) -> Unit
    ) {
        bluetoothController.startAdvertising(
            registry, duration
        ) {
            callback(it)
            it?.let {
                changeDeviceNameToPattern()
                startBluetoothServerAndRoutePacketsToTransactionController()
            }
        }
    }

    fun stopAdvertising(registry: ActivityResultRegistry) {
        bluetoothController.stopAdvertising(registry)
        revertDeviceName()
    }

    fun acceptOffer() {
        scope.launch { transactionController.sendAmountRequestApproval() }
    }

    fun rejectOffer() {
        scope.launch { transactionController.sendAmountRequestRejection() }
    }

    fun disconnect() {
        bluetoothController.closeConnection()
        cancelJob()
        revertDeviceName()
//      transactionController.resetController() is invoked automatically in onCompletion{} for bl packets flow
    }

    fun updateLocalUserInfo() = transactionController.updateLocalUserInfo()

    fun reset() {
        scope.cancelChildren()
        cancelJob()
        transactionController.resetController()
        bluetoothController.reset()
    }

    //Will restart everytime advertising is started
    private fun startBluetoothServerAndRoutePacketsToTransactionController() {
        cancelJob()
        connectionJob =
            bluetoothController.startBluetoothServerAndGetFlow()
                .onEach { connectionResult ->
                    when (connectionResult) {
                        is ConnectionEstablished -> {
                            revertDeviceName()
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
                                .logOut("Received DataPacketVariant:\n", tag = "P2PReceiveUseCase")
                        )
                    }
                }
                .onCompletion { withContext(NonCancellable) { transactionController.resetController() } }
                .launchIn(scope)
    }

    private fun startSendingPacketsFromTransactionController(): Boolean {
        return if (bluetoothState.value.connectionStatus == BluetoothConnectionStatus.CONNECTED) {
            packetsToSend.onEach { packet ->
                this@P2PReceiveUseCase.log("BLUETOOTH conn status = ${bluetoothState.value.connectionStatus}\nGoing to send packet: ${packet.packetType}")
                if (bluetoothState.value.connectionStatus == BluetoothConnectionStatus.CONNECTED) bluetoothController.trySendBytes(
                    packet.toSerializedDataPacket()
                )
//                else throw Exception(
//                    "Tried sending packets from transaction controller but no remote device is connected"
//                )
            }.flowOn(Dispatchers.IO).launchIn(scope)
            true
        } else false
    }

    private fun cancelJob() {
        connectionJob?.cancel()
        connectionJob = null
    }


    private suspend fun wasNameChanged() =
        utilityDataStoreRepository.readValueOrDefault(IS_BLUETOOTH_NAME_CHANGED)

    private suspend fun setNameChanged(value: Boolean) =
        utilityDataStoreRepository.writeValue(IS_BLUETOOTH_NAME_CHANGED, value)

    private fun changeDeviceNameToPattern() {
        scope.launch {
            if (wasNameChanged()) this@P2PReceiveUseCase.log("Bluetooth name already changed")
            else withContext(NonCancellable) {
                this@P2PReceiveUseCase.log("Changing bluetooth name to pattern")
                val currentName = bluetoothController.getDeviceName()
                val prefix = BluetoothController.DEVICE_NAME_PREFIX
                val appUserName = defaultDataStoreRepository.readValueOrDefault(USER_NAME)
                val newName: String = prefix + appUserName
                utilityDataStoreRepository.writeValue(CACHED_BLUETOOTH_NAME, currentName)
                if (bluetoothController.setDeviceName(newName)) {
                    this@P2PReceiveUseCase.log("Name changed successfuly! Old name: $currentName . New name: $newName")
                    setNameChanged(true)
                } else {
                    this@P2PReceiveUseCase.log("Couldn't set a new bluetooth name")
                    _useCaseErrors.emit("Couldn't set a new bluetooth name")
                }
            }
        }
    }

    private fun revertDeviceName() {
        scope.launch {
            if (wasNameChanged())
                withContext(NonCancellable) {
                    this@P2PReceiveUseCase.log("Reverting bluetooth name")
                    val oldDeviceName =
                        utilityDataStoreRepository.readValueOrDefault(CACHED_BLUETOOTH_NAME)
                    if (bluetoothController.setDeviceName(oldDeviceName)) {
                        this@P2PReceiveUseCase.log("Name reverted successfully")
                        setNameChanged(false)
                    } else {
                        this@P2PReceiveUseCase.log("Couldn't revert to old bluetooth name")
                        _useCaseErrors.emit("Couldn't revert to old bluetooth name")
                    }
                }
            else this@P2PReceiveUseCase.log("Bluetooth name is not in pattern form right now, not reverting")

        }
    }

}