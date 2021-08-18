package npo.kib.odc_demo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import npo.kib.odc_demo.data.BankRepository

class ExchangeViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = BankRepository(application)

    val connectionResult = repo.connectionResult
    private val _sum = repo.getSum()
    val sum: StateFlow<Int?> = _sum.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun issueBanknotes(amount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.issueBanknotes(amount)
        }
    }

    fun startAdvertising() {
        repo.startAdvertising()
    }

    fun startDiscovery() {
        repo.startDiscovery()
    }

    fun acceptConnection() {
        repo.acceptConnection()
    }

    fun rejectConnection() {
        repo.rejectConnection()
    }

    fun send(amount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.sendBanknotes(amount)
        }
    }
}