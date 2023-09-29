package npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import npo.kib.odc_demo.feature_app.domain.model.user.AppUser
import npo.kib.odc_demo.feature_app.domain.use_cases.FeatureAppUseCases
import javax.inject.Inject

@HiltViewModel
class ReceiveViewModelNew @Inject constructor(appUseCases: FeatureAppUseCases
                                              /*,savedStateHandle: SavedStateHandle*/) :
    ViewModel() {

    var receiveUiState: ReceiveUiState by mutableStateOf(ReceiveUiState.Advertising)
        private set

    init {

    }

    fun onEvent(event: ReceiveScreenEvent) {
        when (event) {
            is ReceiveScreenEvent.Cancel -> TODO()
            is ReceiveScreenEvent.ChangeAmountFieldFocus -> TODO()
            is ReceiveScreenEvent.ChooseUser -> TODO()
            is ReceiveScreenEvent.ConfirmAmount -> TODO()
            is ReceiveScreenEvent.EnterAmount -> TODO()
            is ReceiveScreenEvent.Finish -> TODO()
            is ReceiveScreenEvent.RetryOnInterrupted -> TODO()
            is ReceiveScreenEvent.StartSearching -> TODO()
        }
    }
}

//change to sealed class?
// Add separate connection buffer class with builder to keep the current connection data
//like current sending user info, current chosen user, the selected banknotes amount, etc, to build
// along with the connection progression?
sealed interface ReceiveUiState {
    object Advertising : ReceiveUiState

    data class ShowingUsersList(val usersList: List<AppUser>) : ReceiveUiState
    object Connecting : ReceiveUiState
    object Connected : ReceiveUiState
    object Interrupted : ReceiveUiState
    object Accepted : ReceiveUiState
    object Rejected : ReceiveUiState
    object Receiving : ReceiveUiState
    object Success : ReceiveUiState
}

