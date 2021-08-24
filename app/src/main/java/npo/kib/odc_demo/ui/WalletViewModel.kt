package npo.kib.odc_demo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import npo.kib.odc_demo.App
import npo.kib.odc_demo.data.BankRepository
import npo.kib.odc_demo.data.models.ServerConnectionStatus

class WalletViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BankRepository(application as App)

    private val _sum: MutableStateFlow<Int?> = MutableStateFlow(0)
    val sum: StateFlow<Int?> = _sum

    private val _serverConnectionStatus: MutableStateFlow<ServerConnectionStatus> =
        MutableStateFlow(ServerConnectionStatus.SUCCESS)
    val serverConnectionStatus: StateFlow<ServerConnectionStatus> = _serverConnectionStatus


    fun getSum() {
        viewModelScope.launch(Dispatchers.IO) {
            _sum.update { repository.getSum() }
        }
    }

    fun issueBanknotes(amount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _serverConnectionStatus.update { ServerConnectionStatus.LOADING }
            val result = repository.issueBanknotes(amount)
            _serverConnectionStatus.update { result }
        }
    }

    fun isWalletRegistered() = repository.isWalletRegistered()
}