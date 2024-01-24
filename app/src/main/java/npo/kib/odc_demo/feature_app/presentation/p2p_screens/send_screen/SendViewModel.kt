package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import npo.kib.odc_demo.feature_app.domain.transaction_logic.SenderTransactionController
import javax.inject.Inject


@HiltViewModel
class SendViewModel @Inject constructor(
    _transactionController: SenderTransactionController
) : ViewModel() {

    private val transactionController: SenderTransactionController = _transactionController
//    val p2pBluetoothConnection = transactionController.p2pConnection


//    private val _uiState: MutableStateFlow<SendUiState> = MutableStateFlow(SendUiState.Initial)

//    private val _state: MutableStateFlow<SendScreenState> =
//        /*MutableStateFlow(SendScreenState())*/
//        combine

    private val _state: MutableStateFlow<SendScreenState> = MutableStateFlow(SendScreenState())


    //TODO combine
    val state: StateFlow<SendScreenState>
        get() = _state.asStateFlow()

    private var deviceConnectionJob: Job? = null


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
        _state.update { it.copy(uiState = SendUiState.Searching) }
//        p2pBluetoothConnection.startDiscovery()
    }

    private fun reset() {
        _state.update { it.copy(uiState = SendUiState.Initial) }
//        transactionController.reset()
    }

    private fun connectTouser() {
        _state.update { it.copy(uiState = SendUiState.Connecting) }
    }
}