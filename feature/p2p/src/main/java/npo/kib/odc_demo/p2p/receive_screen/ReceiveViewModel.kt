package npo.kib.odc_demo.p2p.receive_screen

import androidx.activity.result.ActivityResultRegistry
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import npo.kib.odc_demo.datastore.UtilityDataStoreObject.SHOULD_UPDATE_UI_USER_INFO
import npo.kib.odc_demo.feature_app.data.p2p.bluetooth.BluetoothConnectionStatus
import npo.kib.odc_demo.feature_app.data.p2p.bluetooth.BluetoothState
import npo.kib.odc_demo.feature_app.domain.repository.UtilityDataStoreRepository
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionDataBuffer
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionStatus.ReceiverTransactionStatus
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionStatus.ReceiverTransactionStatus.*
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PReceiveUseCase
import npo.kib.odc_demo.feature_app.domain.util.log
import npo.kib.odc_demo.p2p.receive_screen.ReceiveScreenEvent.*
import npo.kib.odc_demo.p2p.receive_screen.ReceiveUiState.*
import npo.kib.odc_demo.p2p.receive_screen.ReceiveUiState.OperationResult.ResultType.Failure
import npo.kib.odc_demo.p2p.receive_screen.ReceiveUiState.OperationResult.ResultType.Success
import npo.kib.odc_demo.p2p.receive_screen.ReceiveViewModel.Companion.ReceiveViewModelFactory

@HiltViewModel(assistedFactory = ReceiveViewModelFactory::class)
class ReceiveViewModel @AssistedInject constructor(
    private val utilDataStore: UtilityDataStoreRepository,
    private val useCase: P2PReceiveUseCase,
    @Assisted private val registry: ActivityResultRegistry
) : ViewModel() {

    private val transactionDataBuffer: StateFlow<TransactionDataBuffer> =
        useCase.transactionDataBuffer

    private val currentTransactionStatus: StateFlow<ReceiverTransactionStatus> =
        useCase.transactionStatus

    private val bluetoothState: StateFlow<BluetoothState> = useCase.bluetoothState

    private val combinedUiState: StateFlow<ReceiveUiState> = combine(
        currentTransactionStatus, bluetoothState
    ) { transactionStatus, blState ->
        when (blState.connectionStatus) {
            BluetoothConnectionStatus.DISCONNECTED -> Initial
            BluetoothConnectionStatus.ADVERTISING -> Advertising
            BluetoothConnectionStatus.DISCOVERING -> {/*should not be discovering as a receiver*/ Loading
            }

            BluetoothConnectionStatus.CONNECTING -> Loading
            BluetoothConnectionStatus.CONNECTED -> {
                when (transactionStatus) {
                    ERROR -> OperationResult(Failure(transactionDataBuffer.value.lastException.toString()))
                    FINISHED_SUCCESSFULLY -> {
                        utilDataStore.writeValue(SHOULD_UPDATE_UI_USER_INFO, true)
                        this@ReceiveViewModel.log("SHOULD_UPDATE_UI_USER_INFO set to TRUE")
                        OperationResult(Success)
                    }

                    else -> InTransaction(status = transactionStatus)
                }
            }
        }
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), Initial
    )

    val state: StateFlow<ReceiveScreenState> = combine(
        combinedUiState, transactionDataBuffer, bluetoothState
    ) { uiState, buffer, btState ->
        ReceiveScreenState(
            uiState = uiState, transactionDataBuffer = buffer, bluetoothState = btState
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), ReceiveScreenState()
    )

    private val vmErrors: MutableSharedFlow<String> = MutableSharedFlow(extraBufferCapacity = 10)
    val errors: SharedFlow<String> = merge(
        useCase.blErrors, useCase.transactionErrors, useCase.useCaseErrors, vmErrors
    ).shareIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000)
    )

    fun onEvent(event: ReceiveScreenEvent) {
        when (event) {
            is SetAdvertising -> when (event.active) {
                true -> startAdvertising()
                false -> stopAdvertising()
            }

            is Disconnect -> disconnect()
            is ReactToOffer -> when (event.accept) {
                true -> acceptOffer()
                false -> rejectOffer()
            }
        }
    }

    private fun startAdvertising() {
        //Duration of 0 corresponds to indefinite advertising. Unrecommended. Stop advertising manually after.
        //Edit: passing 0 actually makes system prompt for default duration (120 seconds)
        useCase.startAdvertising(registry = registry, duration = 30, callback = { resultDuration ->
            resultDuration ?: viewModelScope.launch { vmErrors.emit("Declined advertising prompt") }
        })
    }

    //Due to a bug (?) in Android some devices will start advertising for 120s instead of 1s
    private fun stopAdvertising() {
        viewModelScope.launch {
            useCase.stopAdvertising(registry)
        }
    }

    private fun acceptOffer() {
        useCase.acceptOffer()
    }

    private fun rejectOffer() {
        useCase.rejectOffer()
    }

    //todo can add a list constant containing all the statuses that it is safe to disconnect from
    // popping the backstack with this viewmodel should also only be possible on those safe states
    private fun disconnect() {
        when (val state = state.value.uiState) {
            is OperationResult -> useCase.disconnect()
            is InTransaction -> if (state.status in listOf(
                    WAITING_FOR_OFFER, OFFER_RECEIVED, FINISHED_SUCCESSFULLY, ERROR
                )
            ) useCase.disconnect()

            else -> viewModelScope.launch { vmErrors.emit("Cannot disconnect during critical operations!") }
        }
    }

    fun updateLocalUserInfo() = useCase.updateLocalUserInfo()

    override fun onCleared() {
//        useCase.reset() //crashes the app
        this.log("onCleared()")
        useCase.disconnect()
        super.onCleared()
    }

    companion object {
        @AssistedFactory
        interface ReceiveViewModelFactory {
            fun create(registry: ActivityResultRegistry): ReceiveViewModel
        }
    }
}