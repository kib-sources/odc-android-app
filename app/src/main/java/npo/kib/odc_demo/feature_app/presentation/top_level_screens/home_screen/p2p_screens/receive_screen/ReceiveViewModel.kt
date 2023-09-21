package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.p2p_screens.receive_screen

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.use_cases.FeatureAppUseCases
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.p2p_screens.nearby_screen.BaseNearbyViewModel
import javax.inject.Inject

@HiltViewModel
class ReceiveViewModel @Inject constructor(walletRepository: WalletRepository, appUseCases: FeatureAppUseCases) : BaseNearbyViewModel(walletRepository) {
    override val p2pUseCase = appUseCases.p2pReceiveUseCase
    val requiringStatusFlow = p2pUseCase.requiringStatusFlow

    fun startDiscovery() {
        p2pUseCase.startDiscovery()
    }

    fun stopDiscovery() {
        p2pUseCase.stopDiscovery()
    }

    fun requireBanknotes(amount: Int) {
        viewModelScope.launch {
            p2pUseCase.requireBanknotes(amount)
        }
    }
}