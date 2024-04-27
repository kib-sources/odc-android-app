package npo.kib.odc_demo.p2p.send_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import npo.kib.odc_demo.common.data.util.log
import npo.kib.odc_demo.datastore.UtilityDataStoreObject.SHOULD_UPDATE_UI_USER_INFO
import npo.kib.odc_demo.datastore.UtilityDataStoreRepository
import npo.kib.odc_demo.domain.P2PSendUseCase
import npo.kib.odc_demo.connectivity.bluetooth.BluetoothConnectionStatus
import npo.kib.odc_demo.connectivity.bluetooth.BluetoothState
import npo.kib.odc_demo.model.CustomBluetoothDevice
import npo.kib.odc_demo.transaction_logic.model.TransactionDataBuffer
import npo.kib.odc_demo.p2p.send_screen.SendScreenEvent.*
import npo.kib.odc_demo.p2p.send_screen.SendUiState.*
import npo.kib.odc_demo.p2p.send_screen.SendUiState.OperationResult.ResultType.Failure
import npo.kib.odc_demo.p2p.send_screen.SendUiState.OperationResult.ResultType.Success
import npo.kib.odc_demo.transaction_logic.model.TransactionStatus.SenderTransactionStatus
import npo.kib.odc_demo.transaction_logic.model.TransactionStatus.SenderTransactionStatus.*
import javax.inject.Inject

@HiltViewModel
class SendViewModel @Inject constructor(
    private val utilDataStore: UtilityDataStoreRepository,
    private val useCase: P2PSendUseCase
) : ViewModel() {
    private var amountConstructionJob: Job? = null

    private val transactionDataBuffer: StateFlow<TransactionDataBuffer> =
        useCase.transactionDataBuffer

    private val currentTransactionStatus: StateFlow<SenderTransactionStatus> =
        useCase.transactionStatus

    private val bluetoothState: StateFlow<BluetoothState> = useCase.bluetoothState

    private val combinedUiState: StateFlow<SendUiState> = combine(
        currentTransactionStatus, bluetoothState
    ) { transactionStatus, blState ->
        when (blState.connectionStatus) {
            BluetoothConnectionStatus.DISCONNECTED -> Initial
            BluetoothConnectionStatus.ADVERTISING -> {/*should not be advertising as a sender*/ Loading
            }

            BluetoothConnectionStatus.DISCOVERING -> Discovering
            BluetoothConnectionStatus.CONNECTING -> Loading
            BluetoothConnectionStatus.CONNECTED -> {
                when (transactionStatus) {
                    ERROR -> OperationResult(Failure(transactionDataBuffer.value.lastException.toString()))
                    FINISHED_SUCCESSFULLY -> {
                        utilDataStore.writeValue(SHOULD_UPDATE_UI_USER_INFO, true)
                        this@SendViewModel.log("SHOULD_UPDATE_UI_USER_INFO set to TRUE")
                        OperationResult(Success)
                    }

                    else -> InTransaction(status = transactionStatus)
                }
            }
        }
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), Initial
    )

    val state: StateFlow<SendScreenState> = combine(
        combinedUiState, transactionDataBuffer, bluetoothState
    ) { uiState, buffer, btState ->
        SendScreenState(
            uiState = uiState, transactionDataBuffer = buffer, bluetoothState = btState
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), SendScreenState()
    )

    private val vmErrors: MutableSharedFlow<String> = MutableSharedFlow(extraBufferCapacity = 10)
    val errors: SharedFlow<String> = merge(
        useCase.blErrors, useCase.transactionErrors, useCase.useCaseErrors, vmErrors
    ).shareIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000)
    )

    fun onEvent(event: SendScreenEvent) {
        when (event) {
            is SetDiscovering -> when (event.active) {
                true -> startDiscovering()
                false -> stopDiscovering()
            }

            Disconnect -> disconnect()
            is ConnectToDevice -> connectToDevice(device = event.device)
            is TryConstructAmount -> tryConstructAmount(event.amount)
            CancelConstructingAmount -> cancelAmountConstructionJob()
            TrySendOffer -> trySendOffer()
        }
    }

    private fun startDiscovering() {
        useCase.startDiscovery()
    }

    private fun stopDiscovering() {
        useCase.stopDiscovery()
    }

    private fun connectToDevice(device: CustomBluetoothDevice) {
        useCase.connectToDevice(device)
    }

    private fun tryConstructAmount(amount: Int) {
        amountConstructionJob = viewModelScope.launch { useCase.tryConstructAmount(amount) }
    }

    private fun trySendOffer() {
        useCase.trySendOffer()
    }

    //todo can create a list of states where it is safe to disconnect or pop the backstack with this viewmodel
    private fun disconnect() {
        when (val state = state.value.uiState) {
            is OperationResult -> useCase.disconnect()
            is InTransaction -> if (listOf(
                    INITIAL,
                    CONSTRUCTING_AMOUNT,
                    SHOWING_AMOUNT_AVAILABILITY,
                    WAITING_FOR_OFFER_RESPONSE,
                    OFFER_REJECTED,
                    FINISHED_SUCCESSFULLY,
                    ERROR
                ).contains(state.status)
            ) useCase.disconnect()

            else -> viewModelScope.launch { vmErrors.emit("Cannot disconnect during critical operations!") }
        }
    }

    private fun cancelAmountConstructionJob() {
        amountConstructionJob?.cancel()
        amountConstructionJob = null
    }

    fun updateLocalUserInfo() = useCase.updateLocalUserInfo()

    override fun onCleared() {
//        useCase.reset() crashes the app
        this.log("onCleared()")
        useCase.disconnect()
        super.onCleared()
    }
}