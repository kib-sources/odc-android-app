package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.p2p_screens.send_screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.p2p_screens.nearby_screen.BaseP2PUiState
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.p2p_screens.request_screen.RequestScreenEvent
import javax.inject.Inject


@HiltViewModel
class SendViewModelNew @Inject constructor() : ViewModel() {

    fun onEvent(event: SendScreenEvent){
        when (event){
            is SendScreenEvent.Cancel -> TODO()
            is SendScreenEvent.ChangeAmountFieldFocus -> TODO()
            is SendScreenEvent.ChooseUser -> TODO()
            is SendScreenEvent.ConfirmAmount -> TODO()
            is SendScreenEvent.EnterAmount -> TODO()
            is SendScreenEvent.Finish -> TODO()
            is SendScreenEvent.RetryOnInterrupted -> TODO()
            is SendScreenEvent.StartSearching -> TODO()
        }
    }
}

sealed interface SendUiState {
    object Searching : SendUiState
    object Connected : SendUiState
    object Interrupted : SendUiState
    object Accepted : SendUiState
    object Rejected : SendUiState
    object Sending : SendUiState
    object Success : SendUiState
}