package npo.kib.odc_demo.feature_app.domain.transaction_logic

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnection
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForReceiver
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForReceiver.END
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForReceiver.INIT_BLOCK
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForReceiver.MAKE_LOCAL_PUSH
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForReceiver.REJECT_OFFER
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForReceiver.SEND_RESULT
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForReceiver.SEND_UNSIGNED_BLOCK
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForReceiver.VERIFY_SIGNATURE
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForReceiver.WAITING_FOR_BANKNOTES
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForReceiver.WAITING_FOR_OFFER
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.TransactionRole.Receiver

abstract class ReceiverTransactionController(private val p2PConnection: P2PConnection) :
    TransactionController(role = Receiver) {

    private val _currentStep : MutableStateFlow<ForReceiver> = MutableStateFlow(WAITING_FOR_OFFER)
    override val currentStep: StateFlow<ForReceiver> = _currentStep.asStateFlow()

    suspend fun doStep(step: ForReceiver) {
        when(step){
            WAITING_FOR_OFFER -> TODO()
            REJECT_OFFER -> sendRejection()
            WAITING_FOR_BANKNOTES -> TODO()
            INIT_BLOCK -> TODO()
            SEND_UNSIGNED_BLOCK -> TODO()
            VERIFY_SIGNATURE -> TODO()
            MAKE_LOCAL_PUSH -> TODO()
            SEND_RESULT -> TODO()
            END -> TODO()
        }

    }


    private fun sendRejection() {


    }





}