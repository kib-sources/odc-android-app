package npo.kib.odc_demo.feature_app.presentation.p2p_screens.nearby_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PBaseUseCase

abstract class BaseP2PViewModel : ViewModel() {

    protected abstract val p2pUseCase: P2PBaseUseCase

    private val walletRepository: WalletRepository by lazy { p2pUseCase.walletRepository }


    private val _sum: MutableStateFlow<Int?> = MutableStateFlow(0)
    val sum: StateFlow<Int?> = _sum

    fun updateCurrentSum() {
        viewModelScope.launch(Dispatchers.IO) {
            _sum.update { walletRepository.getStoredInWalletSum() }
        }
    }

    fun acceptConnection() {
        p2pUseCase.acceptConnection()
    }

    fun rejectConnection() {
        p2pUseCase.rejectConnection()
    }
}