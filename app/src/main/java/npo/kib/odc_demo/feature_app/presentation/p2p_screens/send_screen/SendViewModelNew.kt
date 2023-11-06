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
class SendViewModelNew @Inject constructor(@SendUseCase p2pUseCase: P2PBaseUseCase) :
    BaseP2PViewModel() {


    override val p2pUseCase: P2PSendUseCase = p2pUseCase as P2PSendUseCase


    private val _uiState: MutableStateFlow<SendUiState> =
        MutableStateFlow(SendUiState.Initial)
    val uiState: StateFlow<SendUiState>
        get() = _uiState.asStateFlow()

    private var currentJob: Job? = null


    fun onEvent(event: SendScreenEvent) {
        when (event) {
            is SendScreenEvent.StartSearching -> TODO()
            is SendScreenEvent.ChooseUser -> TODO()
            is SendScreenEvent.ChangeAmountFieldFocus -> TODO()
            is SendScreenEvent.EnterAmount -> TODO()
            is SendScreenEvent.ConfirmAmount -> TODO()
            is SendScreenEvent.Cancel -> TODO()
            is SendScreenEvent.Finish -> TODO()
            is SendScreenEvent.RetryOnInterrupted -> TODO()
        }
    }
}

sealed interface SendUiState {
    object Initial : SendUiState
    object Searching : SendUiState
    data class ShowingUsersList(val usersList: List<AppUser>) : SendUiState
    object Connecting : SendUiState
    object ConnectionAccepted : SendUiState
    object ConnectionRejected : SendUiState
    object Connected : SendUiState
    object SendAccepted : SendUiState
    object SendRejected : SendUiState
    object Sending : SendUiState
    object Success : SendUiState
    object Finish : SendUiState
    object Interrupted : SendUiState
    object Retry : SendUiState
}




