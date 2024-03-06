package npo.kib.odc_demo.feature_app.presentation.p2p_screens.atm_screen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class ATMViewModelNew @Inject constructor() : ViewModel(){
    private val _uiState: MutableStateFlow<ATMUiState> =
        MutableStateFlow(ATMUiState.Initial)
    val uiState: StateFlow<ATMUiState>
        get() = _uiState.asStateFlow()

    fun onEvent(event: ATMScreenEvent){

    }
}

sealed interface ATMUiState {
    data object Initial : ATMUiState
    data object Advertising : ATMUiState
    data object Receiving : ATMUiState
    data class Result(val result: ResultType) : ATMUiState

    sealed interface ResultType {
        data object Success : ResultType
        data class Failure(val failureMessage: String) : ResultType
    }
}