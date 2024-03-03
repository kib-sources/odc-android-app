package npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen

import androidx.activity.result.ActivityResultRegistry
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.data.p2p.bluetooth.BluetoothConnectionStatus
import npo.kib.odc_demo.feature_app.data.p2p.bluetooth.BluetoothState
import npo.kib.odc_demo.feature_app.domain.transaction_logic.ReceiverTransactionStatus
import npo.kib.odc_demo.feature_app.domain.transaction_logic.ReceiverTransactionStatus.*
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionDataBuffer
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PReceiveUseCaseNew
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveScreenEvent.*
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveUiState.*
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveUiState.OperationResult.ResultType.*
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveViewModel.Companion.ReceiveViewModelFactory

@HiltViewModel(assistedFactory = ReceiveViewModelFactory::class)
class ReceiveViewModel @AssistedInject constructor(
    private val useCase: P2PReceiveUseCaseNew,
    @Assisted private val registry: ActivityResultRegistry
) : ViewModel() {

    private val transactionDataBuffer: StateFlow<TransactionDataBuffer> =
        useCase.transactionDataBuffer

    private val currentTransactionStatus: StateFlow<ReceiverTransactionStatus> =
        useCase.transactionStatus

    private val bluetoothState: StateFlow<BluetoothState> = useCase.bluetoothState

    private val combinedUiState: StateFlow<ReceiveUiState> = combine(
        currentTransactionStatus,
        bluetoothState
    ) { transactionStatus, blState ->
        when (blState.connectionStatus) {
            BluetoothConnectionStatus.DISCONNECTED -> Initial
            BluetoothConnectionStatus.ADVERTISING -> Advertising
            BluetoothConnectionStatus.DISCOVERING -> {/*should not be discovering as a receiver*/ Loading
            }
            BluetoothConnectionStatus.CONNECTING -> Loading
            BluetoothConnectionStatus.CONNECTED -> {
                when (transactionStatus) {
                    ERROR -> OperationResult(Failure("Transaction Error"))
                    FINISHED_SUCCESSFULLY -> OperationResult(Success)
                    else -> InTransaction(status = transactionStatus)
                }
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Initial
    )

    val state: StateFlow<ReceiveScreenState> = combine(
        combinedUiState,
        transactionDataBuffer,
        bluetoothState
    ) { uiState, buffer, btState ->
        ReceiveScreenState(
            uiState = uiState,
            transactionDataBuffer = buffer,
            bluetoothState = btState
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        ReceiveScreenState()
    )

    private val vmErrors: MutableSharedFlow<String> = MutableSharedFlow(extraBufferCapacity = 10)
    val errors: SharedFlow<String> = merge(
        useCase.blErrors,
        useCase.transactionErrors,
        vmErrors
    ).shareIn(
        viewModelScope,
        SharingStarted.Lazily
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
        useCase.startAdvertising(registry = registry,
            duration = 10,
            callback = { resultDuration ->
                resultDuration
                    ?: viewModelScope.launch { vmErrors.emit("Declined advertising prompt") }
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

    private fun disconnect() {
        when (val state = state.value.uiState) {
            Connected, is OperationResult -> useCase.disconnect()
            is InTransaction -> if (listOf(
                    OFFER_RECEIVED,
                    FINISHED_SUCCESSFULLY,
                    ERROR
                ).contains(state.status)
            ) useCase.disconnect()
            else -> viewModelScope.launch { vmErrors.emit("Cannot disconnect during critical operations!") }
        }
    }

    override fun onCleared() {
        useCase.reset()
        super.onCleared()
    }

    companion object {
        @AssistedFactory
        interface ReceiveViewModelFactory {
            fun create(registry: ActivityResultRegistry): ReceiveViewModel
        }

        fun provideReceiveViewModelNewFactory(
            factory: ReceiveViewModelFactory,
            registry: ActivityResultRegistry
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {

                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return factory.create(registry) as T
                }

            }
        }
    }
}