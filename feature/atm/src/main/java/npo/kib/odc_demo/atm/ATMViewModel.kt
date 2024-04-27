package npo.kib.odc_demo.atm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import npo.kib.odc_demo.atm.ATMScreenEvent.SendAmountRequestToServer
import npo.kib.odc_demo.datastore.UtilityDataStoreObject.SHOULD_UPDATE_UI_USER_INFO
import npo.kib.odc_demo.atm.ATMUiState.*
import npo.kib.odc_demo.atm.ATMUiState.ResultType.Failure
import npo.kib.odc_demo.atm.ATMUiState.ResultType.Success
import npo.kib.odc_demo.common.data.util.log
import npo.kib.odc_demo.datastore.UtilityDataStoreRepository
import npo.kib.odc_demo.network.api.ServerConnectionStatus.*
import npo.kib.odc_demo.wallet_repository.repository.WalletRepository
import javax.inject.Inject

@HiltViewModel
class ATMViewModel @Inject constructor(
    private val utilDataStore: UtilityDataStoreRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<ATMUiState> = MutableStateFlow(Initial)
    val uiState: StateFlow<ATMUiState>
        get() = _uiState.asStateFlow()

    fun onEvent(event: ATMScreenEvent) {
        when (event) {
            is SendAmountRequestToServer -> sendAmountRequestToServer(event.amount)
        }
    }

    private fun sendAmountRequestToServer(amount: Int) {
        viewModelScope.launch {
            this@ATMViewModel.log("Sending amount request to server")
            _uiState.value = Waiting
            val result = walletRepository.issueBanknotes(amount)
            _uiState.value = when (result) {
                ERROR -> Result(Failure("Server or local error"))
                WALLET_ERROR -> Result(Failure("Wallet error"))
                SUCCESS -> {
                    utilDataStore.writeValue(SHOULD_UPDATE_UI_USER_INFO, true)
                    this@ATMViewModel.log("SHOULD_UPDATE_UI_USER_INFO set to TRUE")
                    Result(Success)
                }
            }
            this@ATMViewModel.log("Request result: $result ")
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