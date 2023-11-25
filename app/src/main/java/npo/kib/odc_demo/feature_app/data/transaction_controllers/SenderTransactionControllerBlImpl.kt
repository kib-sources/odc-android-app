package npo.kib.odc_demo.feature_app.data.transaction_controllers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnection
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.transaction_logic.SenderTransactionController
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionDataBuffer

class SenderTransactionControllerBlImpl(p2PConnection: P2PConnection, walletRepository: WalletRepository) :
    SenderTransactionController(p2PConnection, walletRepository) {

    private val _transactionDataBuffer: MutableStateFlow<TransactionDataBuffer> =
        MutableStateFlow(TransactionDataBuffer())
    override val transactionDataBuffer: StateFlow<TransactionDataBuffer> = _transactionDataBuffer.asStateFlow()

}