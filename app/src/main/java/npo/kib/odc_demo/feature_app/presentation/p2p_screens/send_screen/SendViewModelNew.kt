package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import npo.kib.odc_demo.feature_app.di.SendUseCase
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PBaseUseCase
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PSendUseCase
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.nearby_screen.BaseP2PViewModel
import javax.inject.Inject


@HiltViewModel
class SendViewModelNew @Inject constructor(
    @SendUseCase
    _p2pUseCase: P2PBaseUseCase
) : BaseP2PViewModel() {

    override val p2pUseCase: P2PSendUseCase = _p2pUseCase as P2PSendUseCase
    val p2pBluetoothConnection = p2pUseCase.p2pConnection

    val amountRequestFlow = _p2pUseCase.amountRequestFlow
//    val isSendingFlow = _p2pUseCase.isSendingFlow

    private val _uiState: MutableStateFlow<SendUiState> = MutableStateFlow(SendUiState.Initial)
    val uiState: StateFlow<SendUiState>
        get() = _uiState.asStateFlow()

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
        _uiState.update { SendUiState.Searching }
//        p2pBluetoothConnection.startDiscovery()
    }

    private fun reset() {
        _uiState.update { SendUiState.Initial }
        //transactionController.reset()
    }

    private fun connectTouser(){
        _uiState.update { SendUiState.Initial }
    }
}