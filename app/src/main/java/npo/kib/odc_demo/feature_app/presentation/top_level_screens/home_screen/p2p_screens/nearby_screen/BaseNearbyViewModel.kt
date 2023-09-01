package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.p2p_screens.nearby_screen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.data.P2PBaseUseCase
import npo.kib.odc_demo.feature_app.data.repositories.WalletRepository
import javax.inject.Inject


abstract class BaseNearbyViewModel(application: Application) : AndroidViewModel(application) {

    protected abstract val p2pUseCase: P2PBaseUseCase
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