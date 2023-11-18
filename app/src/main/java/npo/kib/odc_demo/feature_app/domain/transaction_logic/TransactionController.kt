package npo.kib.odc_demo.feature_app.domain.transaction_logic

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithBlockchain
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.Block
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.TransactionRole

abstract class TransactionController(val role: TransactionRole) {

    abstract val currentStep: StateFlow<TransactionSteps>

    abstract val transactionDataBuffer: StateFlow<TransactionDataBuffer>

    private var currentJob = Job() as Job

    suspend fun acceptance(banknoteWithBlockchain: BanknoteWithBlockchain) {

    }

    suspend fun verifyAndSaveNewBlock(signedBlock: Block) {

    }


}

