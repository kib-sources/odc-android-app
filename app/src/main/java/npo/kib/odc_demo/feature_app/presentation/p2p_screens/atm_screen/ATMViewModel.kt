package npo.kib.odc_demo.feature_app.presentation.p2p_screens.atm_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.domain.model.connection_status.ServerConnectionStatus.*
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.util.log
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.atm_screen.ATMUiState.*
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.atm_screen.ATMUiState.ResultType.*
import javax.inject.Inject

@HiltViewModel
class ATMViewModelNew @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<ATMUiState> = MutableStateFlow(Initial)
    val uiState: StateFlow<ATMUiState>
        get() = _uiState.asStateFlow()

    fun onEvent(event: ATMScreenEvent) {
        when (event) {
            is ATMScreenEvent.SendAmountRequestToServer -> sendAmountRequestToServer(event.amount)
        }
    }

    private fun sendAmountRequestToServer(amount: Int) {
        viewModelScope.launch {
            this@ATMViewModelNew.log("Sending amount request to server")
            _uiState.value = Waiting
            val result = walletRepository.issueBanknotes(amount)
            _uiState.value = when (result) {
                ERROR -> Result(Failure("Server or local error"))
                WALLET_ERROR -> Result(Failure("Wallet error"))
                SUCCESS -> Result(Success)
            }
            this@ATMViewModelNew.log("Request result: $result ")
            delay(3000)
            _uiState.value = Initial
        }
    }

    override fun onCleared() {
        this.log("onCleared()")
        super.onCleared()
    }
}

sealed interface ATMUiState {
    data object Initial : ATMUiState
    data object Waiting : ATMUiState
    data class Result(val result: ResultType) : ATMUiState
    sealed interface ResultType {
        data object Success : ResultType
        data class Failure(val failureMessage: String) : ResultType
    }
}