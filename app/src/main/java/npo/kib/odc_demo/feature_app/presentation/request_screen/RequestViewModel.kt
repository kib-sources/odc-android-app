package npo.kib.odc_demo.feature_app.presentation.request_screen

import android.app.Application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.data.P2PReceiveUseCase
import npo.kib.odc_demo.feature_app.presentation.nearby_screen.BaseNearbyViewModel

class RequestViewModel(application: Application) : BaseNearbyViewModel(application) {
    override val p2pUseCase = P2PReceiveUseCase(application)
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