package npo.kib.odc_demo.feature_app.domain.transaction_logic

import kotlinx.coroutines.flow.StateFlow
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.Block
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.TransactionRole

abstract class TransactionController(val role: TransactionRole) {

    protected abstract val currentStep: StateFlow<TransactionSteps>

    protected abstract val transactionDataBuffer: StateFlow<TransactionDataBuffer>


    suspend fun verifyAndSaveNewBlock(signedBlock: Block) {

    }

    class InvalidStepsOrderException(
        lastStep: TransactionSteps,
        attemptedStep: TransactionSteps
    ) : Exception("""Invalid step order. 
        |Last step was: $lastStep
        |Tried to do step: $attemptedStep""".trimMargin())

}

