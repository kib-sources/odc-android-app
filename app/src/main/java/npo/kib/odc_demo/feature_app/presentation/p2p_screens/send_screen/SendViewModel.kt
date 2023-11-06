package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.di.SendUseCase
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PBaseUseCase
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PReceiveUseCase
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PSendUseCase
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.nearby_screen.BaseP2PViewModel
import javax.inject.Inject

@HiltViewModel
class SendViewModel @Inject constructor(@SendUseCase p2pUseCase: P2PBaseUseCase) :
    BaseP2PViewModel() {

    override val p2pUseCase: P2PSendUseCase = p2pUseCase as P2PSendUseCase

    val amountRequestFlow = p2pUseCase.amountRequestFlow
    val isSendingFlow = p2pUseCase.isSendingFlow


    fun startAdvertising() = Unit

    fun stopAdvertising() = Unit

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