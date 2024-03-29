package npo.kib.odc_demo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import npo.kib.odc_demo.data.P2pBaseUseCase
import npo.kib.odc_demo.data.WalletRepository

abstract class BaseNearbyViewModel(application: Application) : AndroidViewModel(application) {

    protected abstract val p2pUseCase: P2pBaseUseCase
    private val walletRepository = WalletRepository(application)

    val connectionResult by lazy { p2pUseCase.connectionResult }
    val searchingStatusFlow by lazy { p2pUseCase.searchingStatusFlow }

    private val _sum: MutableStateFlow<Int?> = MutableStateFlow(0)
    val sum: StateFlow<Int?> = _sum

    fun getCurrentSum() {
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