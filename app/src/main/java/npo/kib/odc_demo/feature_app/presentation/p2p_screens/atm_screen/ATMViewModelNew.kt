package npo.kib.odc_demo.feature_app.presentation.p2p_screens.atm_screen

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import npo.kib.odc_demo.feature_app.di.AtmUseCase
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PBaseUseCase
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PReceiveUseCase
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.nearby_screen.BaseP2PViewModel
import javax.inject.Inject

class ATMViewModelNew @Inject constructor(@AtmUseCase p2pUseCase: P2PBaseUseCase) : BaseP2PViewModel(){

    override val p2pUseCase = p2pUseCase as P2PReceiveUseCase

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