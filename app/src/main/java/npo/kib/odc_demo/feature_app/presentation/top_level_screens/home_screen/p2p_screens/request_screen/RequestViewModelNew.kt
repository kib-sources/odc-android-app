package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.p2p_screens.request_screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.internal.lifecycle.HiltViewModelMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.p2p_screens.nearby_screen.BaseP2PUiState
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.p2p_screens.send_screen.SendUiState
import javax.inject.Inject

@HiltViewModel
class RequestViewModelNew @Inject constructor() : ViewModel() {

    fun onEvent(event: RequestScreenEvent){
        when (event){
            is RequestScreenEvent.Cancel -> TODO()
            is RequestScreenEvent.ChangeAmountFieldFocus -> TODO()
            is RequestScreenEvent.ChooseUser -> TODO()
            is RequestScreenEvent.ConfirmAmount -> TODO()
            is RequestScreenEvent.EnterAmount -> TODO()
            is RequestScreenEvent.Finish -> TODO()
            is RequestScreenEvent.RetryOnInterrupted -> TODO()
            is RequestScreenEvent.StartSearching -> TODO()
        }
    }
}


sealed interface RequestUiState {
    object Searching : RequestUiState
    object Connected : RequestUiState
    object Interrupted : RequestUiState
    object Accepted : RequestUiState
    object Rejected : RequestUiState
    object Receiving : RequestUiState
    object Success : RequestUiState
}

