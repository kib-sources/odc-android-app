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

// TODO: Read about Hilt's @AssistedInject
abstract class BaseNearbyViewModel(private val walletRepository: WalletRepository) : ViewModel() {

    protected abstract val p2pUseCase: P2PBaseUseCase

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