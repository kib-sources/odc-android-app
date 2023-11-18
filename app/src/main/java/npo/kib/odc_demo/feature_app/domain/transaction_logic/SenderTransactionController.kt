package npo.kib.odc_demo.feature_app.domain.transaction_logic

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnection
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.*
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForSender.*
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.TransactionRole.Sender


abstract class SenderTransactionController(private val p2PConnection: P2PConnection) : TransactionController(role = Sender) {

    private val _currentStep : MutableStateFlow<ForSender> = MutableStateFlow(INIT)
    override val currentStep: StateFlow<ForSender> = _currentStep.asStateFlow()

}