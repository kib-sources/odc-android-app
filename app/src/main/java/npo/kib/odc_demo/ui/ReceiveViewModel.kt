package npo.kib.odc_demo.ui

import android.app.Application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import npo.kib.odc_demo.data.P2pReceiveUseCase

class ReceiveViewModel(application: Application) : BaseNearbyViewModel(application) {
    override val p2pUseCase = P2pReceiveUseCase(application)
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