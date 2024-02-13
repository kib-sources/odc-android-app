package npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen

import androidx.activity.result.ActivityResultRegistry
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.data.p2p.bluetooth.BluetoothState
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.BluetoothController
import npo.kib.odc_demo.feature_app.domain.transaction_logic.ReceiverTransactionController
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionDataBuffer
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PReceiveUseCaseNew
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveUiState.Advertising
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveUiState.Connected
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveUiState.Initial
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveUiState.OfferReceived

class ReceiveViewModel @AssistedInject constructor(
    private val useCase: P2PReceiveUseCaseNew,
    @Assisted private val registry: ActivityResultRegistry
) : ViewModel() {

    private val transactionDataBuffer: StateFlow<TransactionDataBuffer> =
        useCase.transactionDataBuffer
    private val bluetoothState: StateFlow<BluetoothState> = useCase.bluetoothState

    private val _uiState: MutableStateFlow<ReceiveUiState> = MutableStateFlow(Initial)

    val state: StateFlow<ReceiveScreenState> = combine(
        _uiState, transactionDataBuffer, bluetoothState
    ) { uiState, buffer, btState ->
        ReceiveScreenState(
            uiState = uiState, transactionDataBuffer = buffer, bluetoothState = btState
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000), ReceiveScreenState())

    val errors = useCase.errors

    fun onEvent(event: ReceiveScreenEvent) {
        when (event) {
            is ReceiveScreenEvent.SetAdvertising -> {
                if (event.active) startAdvertising()
                else stopAdvertising()
            }

            is ReceiveScreenEvent.Disconnect -> disconnect()

            is ReceiveScreenEvent.ReactToOffer -> {
                if (event.accept) {
                    acceptOffer()
                } else {
                    rejectOffer()
                }
            }

            ReceiveScreenEvent.Reset -> {
                reset()
            }

            ReceiveScreenEvent.Finish -> {

            }
        }

    }


    private fun startAdvertising() {

        //Duration of 0 corresponds to indefinite advertising. Unrecommended. Stop advertising manually after.
        //Edit: passing 0 actually makes system prompt for default duration (120 seconds)
        useCase.startAdvertising(registry = registry, duration = 10, callback = { resultDuration ->
            resultDuration?.run {
                _uiState.update { Advertising }
                viewModelScope.launch {
                    TODO()
                }
//                        deviceConnectionJob?.cancel()
//                        deviceConnectionJob =
//                            p2pBluetoothConnection.startBluetoothServerAndGetFlow().listen()
            }
        })


    }

    //Due to a bug (?) in Android some devices will start advertising for 120s instead of 1s
    private fun stopAdvertising() {
        viewModelScope.launch {
            useCase.stopAdvertising(registry)
//            _uiState.update { ReceiveUiState.Initial }
        }
    }

    private fun disconnect() {
//        if (Connected)||f(OfferReceived)||f(ReceiveUiState.TransactionResult))
        when (state.value.uiState) {
            Connected, OfferReceived, is ReceiveUiState.OperationResult -> {
                useCase.disconnect()
            }

            else -> {/* Should not disconnect during critical operations */
            }
        }
    }

    private fun acceptOffer() {
        //update ui state
        //...
        TODO()
    }

    private fun rejectOffer() {

    }


    private fun reset() {

        useCase.reset()
    }


    override fun onCleared() {
        super.onCleared()
        useCase.reset()
    }

    @AssistedFactory
    interface Factory {
        fun create(registry: ActivityResultRegistry): ReceiveViewModel
    }

    companion object {
        fun provideReceiveViewModelNewFactory(
            factory: Factory, registry: ActivityResultRegistry
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {

                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return factory.create(registry) as T
                }

            }
        }

        val LocalReceiveViewModelFactory = compositionLocalOf<Factory?> { null }
    }
}