package npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen

import dagger.hilt.android.lifecycle.HiltViewModel
import npo.kib.odc_demo.feature_app.di.ReceiveUseCase
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PBaseUseCase
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PReceiveUseCase
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.nearby_screen.BaseP2PViewModel
import javax.inject.Inject

@HiltViewModel
class ReceiveViewModel @Inject constructor(@ReceiveUseCase p2pUseCase: P2PBaseUseCase) : BaseP2PViewModel() {

    override val p2pUseCase : P2PReceiveUseCase = p2pUseCase as P2PReceiveUseCase

//    fun startDiscovery() {
//        p2pUseCase.startDiscovery()
//    }
//
//    fun stopDiscovery() {
//        p2pUseCase.stopDiscovery()
//    }

//    fun requireBanknotes(amount: Int) {
//        viewModelScope.launch {
//            p2pUseCase.requireBanknotes(amount)
//        }
//    }
}