package npo.kib.odc_demo.feature_app.presentation.send_screen

import android.app.Application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.data.P2PSendUseCase
import npo.kib.odc_demo.feature_app.presentation.nearby_screen.BaseNearbyViewModel

class SendViewModel(application: Application) : BaseNearbyViewModel(application) {
    override val p2pUseCase = P2PSendUseCase(application)
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