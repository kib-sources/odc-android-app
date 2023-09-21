package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.data.repositories.BankRepositoryImpl
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PReceiveUseCase
import npo.kib.odc_demo.feature_app.domain.model.types.RequiringStatus
import npo.kib.odc_demo.feature_app.domain.model.types.ServerConnectionStatus
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnection
import npo.kib.odc_demo.feature_app.data.p2p.nfc.P2PConnectionNfcImpl
import npo.kib.odc_demo.feature_app.domain.repository.BankRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: BankRepository,) : ViewModel() {

//    private val p2pTcp: P2PConnection = P2PConnectionTcpImpl(application, "192.168.0.105")
//    private val p2pTcpUseCase = P2PReceiveUseCase(application, p2pTcp)

    //create separate useCase for NFC and add to FeatureAppUseCases
    private val p2pNfc: P2PConnection = P2PConnectionNfcImpl(application)
    private val p2pNfcUseCase = P2PReceiveUseCase(p2pNfc)

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