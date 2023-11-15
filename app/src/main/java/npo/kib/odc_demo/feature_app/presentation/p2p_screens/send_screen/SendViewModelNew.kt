package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import npo.kib.odc_demo.feature_app.di.SendUseCase
import npo.kib.odc_demo.feature_app.domain.model.user.AppUser
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PBaseUseCase
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PSendUseCase
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.nearby_screen.BaseP2PViewModel
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveUiState
import javax.inject.Inject


@HiltViewModel
class SendViewModelNew @Inject constructor(@SendUseCase _p2pUseCase: P2PBaseUseCase) :
    BaseP2PViewModel() {

    override val p2pUseCase: P2PSendUseCase = _p2pUseCase as P2PSendUseCase
    val p2pBluetoothConnection = p2pUseCase.p2pConnection

    val amountRequestFlow = _p2pUseCase.amountRequestFlow
//    val isSendingFlow = _p2pUseCase.isSendingFlow

    private val _uiState: MutableStateFlow<SendUiState> =
        MutableStateFlow(SendUiState.Initial)
    val uiState: StateFlow<SendUiState>
        get() = _uiState.asStateFlow()

    private var deviceConnectionJob: Job? = null


    fun onEvent(event: SendScreenEvent) {
        when (event) {
            is SendScreenEvent.StartSearching -> {
//                _uiState.update { SendUiState.Searching }
            }
            is SendScreenEvent.ChooseUser -> {}
            is SendScreenEvent.ChangeAmountFieldFocus -> {}
            is SendScreenEvent.EnterAmount -> {}
            is SendScreenEvent.ConfirmAmount -> {}
            is SendScreenEvent.Cancel -> {}
            is SendScreenEvent.Finish -> {}
            is SendScreenEvent.Retry -> {}
        }
    }

    private fun startSearching(){
        p2pBluetoothConnection.startDiscovery()
    }

}


sealed interface SendUiState {
    data object Initial : SendUiState
    data class Searching(val usersList: List<AppUser>) : SendUiState
    data object Connecting : SendUiState
    data object ConnectionAccepted : SendUiState
    data object ConnectionRejected : SendUiState
    data object Connected : SendUiState
    data class OfferSent(val accepted: Boolean) : SendUiState
    data object Sending : SendUiState
    data class Result(val result: ReceiveUiState.ResultType) : SendUiState
    data object Retry : SendUiState
    sealed interface ResultType {
        data object Success : ResultType
        data class Failure(val failureMessage: String) : ResultType
    }
}




