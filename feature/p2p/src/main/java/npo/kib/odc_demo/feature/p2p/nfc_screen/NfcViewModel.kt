package npo.kib.odc_demo.feature.p2p.nfc_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import npo.kib.odc_demo.core.common.data.util.myLogs
import npo.kib.odc_demo.core.domain.P2PNfcUseCase
import npo.kib.odc_demo.core.connectivity.ConnectingStatus
import javax.inject.Inject

@HiltViewModel
internal class NfcViewModel @Inject constructor(
    private val nfcUseCase: P2PNfcUseCase
): ViewModel() {

    private fun getBanknotesFromAtmByNfc(){
        nfcUseCase.startDiscovery()
        viewModelScope.launch(IO) {
            nfcUseCase.nfcState.collect{ status ->
                myLogs(status, "NFC VIEW MODEL")
                when(status){
                    is ConnectingStatus.ConnectionInitiated -> {
                        // TODO()
                    }
                    is ConnectingStatus.ConnectionResult -> {
                        nfcUseCase.stopDiscovery()
                    }
                    ConnectingStatus.Disconnected -> {
                        // TODO()
                    }
                    ConnectingStatus.NoConnection -> {
                        // TODO()
                    }
                }
            }
        }
    }

    fun onEvent(event: NfcScreenEvent) {
        when(event){
            NfcScreenEvent.StartReceiving -> getBanknotesFromAtmByNfc()
        }
    }

}