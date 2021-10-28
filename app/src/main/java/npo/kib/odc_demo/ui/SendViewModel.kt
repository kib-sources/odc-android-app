package npo.kib.odc_demo.ui

import android.app.Application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import npo.kib.odc_demo.data.P2pSendUseCase

class SendViewModel(application: Application) : BaseNearbyViewModel(application) {
    override val p2pUseCase = P2pSendUseCase(application)
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