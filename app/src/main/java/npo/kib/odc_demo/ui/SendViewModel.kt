package npo.kib.odc_demo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import npo.kib.odc_demo.data.P2PRepository
import npo.kib.odc_demo.data.WalletRepository

class SendViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = P2PRepository(application)
    private val walletRepository = WalletRepository(application)

    val connectionResult = repository.connectionResult
    val searchingStatusFlow = repository.searchingStatusFlow
    val amountRequestFlow = repository.amountRequestFlow
    val isSendingFlow = repository.isSendingFlow

    private val _sum: MutableStateFlow<Int?> = MutableStateFlow(0)
    val sum: StateFlow<Int?> = _sum

    fun getSum() {
        viewModelScope.launch(Dispatchers.IO) {
            _sum.update { walletRepository.getStoredInWalletSum() }
        }
    }

    fun startAdvertising() {
        repository.startAdvertising()
    }

    fun stopAdvertising() {
        repository.stopAdvertising()
    }

    fun acceptConnection() {
        repository.acceptConnection()
    }

    fun rejectConnection() {
        repository.rejectConnection()
    }

    fun sendBanknotes(amount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sendBanknotes(amount)
        }
    }

    fun sendRejection() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sendRejection()
        }
    }
}