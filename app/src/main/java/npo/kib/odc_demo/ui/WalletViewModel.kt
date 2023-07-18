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
import npo.kib.odc_demo.data.p2p.nfc.P2pConnectionNfcImpl

class WalletViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BankRepository(application)

    private val p2pTcp: P2pConnection = P2pConnectionTcpImpl(application, "192.168.0.105")
    private val p2pTcpUseCase = P2pReceiveUseCase(application, p2pTcp)

    private val p2pNfc: P2pConnection = P2pConnectionNfcImpl(application)
    private val p2pNfcUseCase = P2pReceiveUseCase(application, p2pNfc)

    private val _sum: MutableStateFlow<Int?> = MutableStateFlow(0)
   // val sum: StateFlow<Int?> = _sum
    val sum = repository.getSumAsFlow()

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

    fun getBanknotesFromAtmByTcp() {
        p2pTcpUseCase.startDiscovery()
        viewModelScope.launch(Dispatchers.IO) {
            p2pTcpUseCase.requiringStatusFlow.collect {
                when (it) {
                    RequiringStatus.NONE -> Unit
                    RequiringStatus.REQUEST -> Unit
                    RequiringStatus.REJECT -> Unit
                    RequiringStatus.ACCEPTANCE -> Unit
                    RequiringStatus.COMPLETED -> {
                        //p2pTcpUseCase.stopDiscovery()
                        updateSum()
                    }
                }
            }
        }
    }

    fun getBanknotesFromAtmByNfc() {
        p2pNfcUseCase.startDiscovery()
        viewModelScope.launch(Dispatchers.IO) {
            p2pNfcUseCase.requiringStatusFlow.collect {
                when (it) {
                    RequiringStatus.NONE -> Unit
                    RequiringStatus.REQUEST -> Unit
                    RequiringStatus.REJECT -> Unit
                    RequiringStatus.ACCEPTANCE -> {
                        _serverConnectionStatus.update { ServerConnectionStatus.LOADING }
                    }
                    RequiringStatus.COMPLETED -> {
                        p2pTcpUseCase.stopDiscovery()
                        _serverConnectionStatus.update { ServerConnectionStatus.SUCCESS }
                        updateSum()
                    }
                }
            }
        }
    }
}