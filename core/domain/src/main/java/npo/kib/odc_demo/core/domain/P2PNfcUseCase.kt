package npo.kib.odc_demo.core.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import npo.kib.odc_demo.core.common.data.di.P2PUseCaseScope
import npo.kib.odc_demo.core.connectivity.nfc.NfcController
import npo.kib.odc_demo.core.connectivity.ConnectingStatus
import npo.kib.odc_demo.core.transaction_logic.ReceiverTransactionController
import javax.inject.Inject

class P2PNfcUseCase @Inject constructor(
    private val transactionController: ReceiverTransactionController,
    private val nfcController: NfcController,
    @P2PUseCaseScope private val scope: CoroutineScope
) {

    val nfcState = nfcController.connectionResult.stateIn(
        scope, SharingStarted.WhileSubscribed(), ConnectingStatus.NoConnection
    )


    fun startDiscovery() {
        nfcController.startDiscovery()
    }

    fun stopDiscovery(){
        nfcController.stopDiscovery()
    }

}