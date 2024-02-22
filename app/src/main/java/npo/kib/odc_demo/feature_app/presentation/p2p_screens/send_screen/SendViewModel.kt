package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import npo.kib.odc_demo.feature_app.data.p2p.bluetooth.BluetoothState
import npo.kib.odc_demo.feature_app.domain.transaction_logic.SenderTransactionController
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionDataBuffer
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PSendUseCaseNew
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveScreenState
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveUiState
import javax.inject.Inject


@HiltViewModel
class SendViewModel @Inject constructor(
    private val useCase: P2PSendUseCaseNew
) : ViewModel() {

    private val transactionDataBuffer: StateFlow<TransactionDataBuffer> =
        useCase.transactionDataBuffer
    private val bluetoothState: StateFlow<BluetoothState> = useCase.bluetoothState

    private val _uiState: MutableStateFlow<SendUiState> = MutableStateFlow(
        SendUiState.Initial
    )

    val state: StateFlow<SendScreenState> = combine(
        _uiState, transactionDataBuffer, bluetoothState
    ) { uiState, buffer, btState ->
        SendScreenState(
            uiState = uiState, transactionDataBuffer = buffer, bluetoothState = btState
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000), SendScreenState())

    val errors = useCase.errors


    fun onEvent(event: SendScreenEvent) {
        when (event) {
            is SendScreenEvent.StartSearching -> startSearching()
            SendScreenEvent.Reset -> reset()
            is SendScreenEvent.ConnectToUser -> {}
            is SendScreenEvent.SendOffer -> {}
            is SendScreenEvent.Retry -> {}
            is SendScreenEvent.Finish -> {
//             same as reset but when operation is successful, or probably can navigate to p2p root screen on click
            }
        }
    }

    private fun startSearching() {
        //TODO replace with combine where the flow is created (combine with uiState flow)
        _uiState.value = SendUiState.Searching

    }

    private fun reset() {
        _uiState.value = SendUiState.Initial
//        transactionController.reset()
    }

    private fun connectTouser() {
        _uiState.value = SendUiState.Connecting
    }
}