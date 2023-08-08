package npo.kib.odc_demo.feature_app.presentation.home_screen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.data.BankRepository
import npo.kib.odc_demo.feature_app.data.P2PReceiveUseCase
import npo.kib.odc_demo.feature_app.data.models.types.RequiringStatus
import npo.kib.odc_demo.feature_app.data.models.types.ServerConnectionStatus
import npo.kib.odc_demo.feature_app.data.p2p.P2PConnection
import npo.kib.odc_demo.feature_app.data.p2p.nfc.P2PConnectionNfcImpl

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BankRepository(application)

//    private val p2pTcp: P2PConnection = P2PConnectionTcpImpl(application, "192.168.0.105")
//    private val p2pTcpUseCase = P2PReceiveUseCase(application, p2pTcp)

    private val p2pNfc: P2PConnection = P2PConnectionNfcImpl(application)
    private val p2pNfcUseCase = P2PReceiveUseCase(application, p2pNfc)

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


    //????? "get from ATM via WI-FI"?
   /* fun getBanknotesFromAtmByTcp() {
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
    }*/

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
//                        p2pTcpUseCase.stopDiscovery()
                        _serverConnectionStatus.update { ServerConnectionStatus.SUCCESS }
                        updateSum()
                    }
                }
            }
        }
    }
}