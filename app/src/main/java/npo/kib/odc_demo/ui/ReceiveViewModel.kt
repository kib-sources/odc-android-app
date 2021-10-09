package npo.kib.odc_demo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import npo.kib.odc_demo.data.P2pReceiveUseCase
import npo.kib.odc_demo.data.WalletRepository

class ReceiveViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = P2pReceiveUseCase(application)
    private val walletRepository = WalletRepository(application)

    val connectionResult = repository.connectionResult
    val searchingStatusFlow = repository.searchingStatusFlow
    val requiringStatusFlow = repository.requiringStatusFlow

    private val _sum: MutableStateFlow<Int?> = MutableStateFlow(0)
    val sum: StateFlow<Int?> = _sum

    fun getSum() {
        viewModelScope.launch(Dispatchers.IO) {
            _sum.update { walletRepository.getStoredInWalletSum() }
        }
    }

    fun startDiscovery() {
        repository.startDiscovery()
    }

    fun stopDiscovery() {
        repository.stopDiscovery()
    }

    fun acceptConnection() {
        repository.acceptConnection()
    }

    fun rejectConnection() {
        repository.rejectConnection()
    }

    fun requireBanknotes(amount: Int) {
        viewModelScope.launch {
            repository.requireBanknotes(amount)
        }
    }
}