package npo.kib.odc_demo.feature_app.data.p2p.controllers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnection
import npo.kib.odc_demo.feature_app.domain.transaction_logic.ReceiverTransactionController
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionDataBuffer

class ReceiverTransactionControllerImpl(p2PConnection: P2PConnection) :
    ReceiverTransactionController(p2PConnection = p2PConnection) {

    private val _transactionDataBuffer: MutableStateFlow<TransactionDataBuffer> =
        MutableStateFlow(TransactionDataBuffer())
    override val transactionDataBuffer: StateFlow<TransactionDataBuffer> = _transactionDataBuffer.asStateFlow()


}