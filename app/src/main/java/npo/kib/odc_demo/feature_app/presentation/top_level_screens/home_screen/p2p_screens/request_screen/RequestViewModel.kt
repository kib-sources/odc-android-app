package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.p2p_screens.request_screen

import android.app.Application
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.data.P2PReceiveUseCase
import npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.p2p_screens.nearby_screen.BaseNearbyViewModel
import javax.inject.Inject

@HiltViewModel
class RequestViewModel @Inject constructor(application: Application) : BaseNearbyViewModel(application) {
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