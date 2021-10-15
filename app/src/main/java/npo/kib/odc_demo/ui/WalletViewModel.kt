package npo.kib.odc_demo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import npo.kib.odc_demo.data.BankRepository
import npo.kib.odc_demo.data.models.ServerConnectionStatus

class WalletViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BankRepository(application)

    private val _sum: MutableStateFlow<Int?> = MutableStateFlow(0)
    val sum: StateFlow<Int?> = _sum

    private val _serverConnectionStatus = MutableStateFlow(ServerConnectionStatus.SUCCESS)
    val serverConnectionStatus: StateFlow<ServerConnectionStatus> = _serverConnectionStatus

    fun getSum() {
        viewModelScope.launch(Dispatchers.IO) {
            _sum.update { repository.getSum() }
        }
    }

    fun issueBanknotes(amount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _serverConnectionStatus.update { ServerConnectionStatus.LOADING }
            _serverConnectionStatus.update { repository.issueBanknotes(amount) }
        }
    }

    fun isWalletRegistered() = repository.isWalletRegistered()
}