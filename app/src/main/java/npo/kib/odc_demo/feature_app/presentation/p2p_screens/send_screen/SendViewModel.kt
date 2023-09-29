package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.use_cases.FeatureAppUseCases
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.nearby_screen.BaseNearbyViewModel
import javax.inject.Inject

@HiltViewModel
class SendViewModel @Inject constructor(walletRepository: WalletRepository, appUseCases: FeatureAppUseCases) : BaseNearbyViewModel(walletRepository = walletRepository) {
    override val p2pUseCase = appUseCases.p2pSendUseCase
    val amountRequestFlow = p2pUseCase.amountRequestFlow
    val isSendingFlow = p2pUseCase.isSendingFlow

    fun startAdvertising() {
        p2pUseCase.startAdvertising()
    }

    fun stopAdvertising() {
        p2pUseCase.stopAdvertising()
    }

    fun sendBanknotes(amount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            p2pUseCase.sendBanknotes(amount)
        }
    }

    fun sendRejection() {
        viewModelScope.launch(Dispatchers.IO) {
            p2pUseCase.sendRejection()
        }
    }
}