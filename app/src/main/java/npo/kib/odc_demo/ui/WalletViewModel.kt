package npo.kib.odc_demo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import npo.kib.odc_demo.data.BankRepository
import npo.kib.odc_demo.data.P2pReceiveUseCase
import npo.kib.odc_demo.data.models.RequiringStatus
import npo.kib.odc_demo.data.models.ServerConnectionStatus
import npo.kib.odc_demo.data.p2p.P2pConnection
import npo.kib.odc_demo.data.p2p.P2pConnectionTcpImpl

class WalletViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BankRepository(application)

    private val p2p: P2pConnection = P2pConnectionTcpImpl(application, "192.168.1.117")
    private val p2pUseCase = P2pReceiveUseCase(application, p2p)

    private val _sum: MutableStateFlow<Int?> = MutableStateFlow(0)
    val sum: StateFlow<Int?> = _sum

    private val _serverConnectionStatus = MutableStateFlow(ServerConnectionStatus.SUCCESS)
    val serverConnectionStatus: StateFlow<ServerConnectionStatus> = _serverConnectionStatus

    fun updateSum() {
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

    fun getBanknotesFromATM() {
        p2pUseCase.startDiscovery()
        viewModelScope.launch(Dispatchers.IO) {
            p2pUseCase.requiringStatusFlow.collect {
                when(it) {
                    RequiringStatus.NONE -> Unit
                    RequiringStatus.REQUEST -> Unit
                    RequiringStatus.REJECT -> Unit
                    RequiringStatus.ACCEPTANCE -> Unit
                    RequiringStatus.COMPLETED -> {
                        p2pUseCase.stopDiscovery()
                        updateSum()
                    }
                }
            }
        }
    }
}