package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.p2p_screens.send_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import npo.kib.odc_demo.feature_app.domain.model.user.AppUser
import npo.kib.odc_demo.feature_app.domain.use_cases.FeatureAppUseCases
import javax.inject.Inject


@HiltViewModel
class SendViewModelNew @Inject constructor(appUseCases: FeatureAppUseCases /*, savedStateHandle: SavedStateHandle*/) :
    ViewModel() {

    //Use Hilt to provide? Use cases are called inside the controller
//    val sendStateController = SendStateController
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




