package npo.kib.odc_demo.feature_app.domain.transaction_logic

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.DataPacketType
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.DataPacketVariant
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.TransactionResult
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.TransactionResult.ResultType
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.UserInfo
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.TransactionRole

abstract class TransactionController(val role: TransactionRole) {

    protected val _transactionDataBuffer: MutableStateFlow<TransactionDataBuffer> = MutableStateFlow(
        TransactionDataBuffer()
    )

    val transactionDataBuffer: StateFlow<TransactionDataBuffer> = _transactionDataBuffer.asStateFlow()

    protected val _outputDataPacketChannel: Channel<DataPacketVariant> = Channel(capacity = UNLIMITED)
    val outputDataPacketFlow: Flow<DataPacketVariant> = _outputDataPacketChannel.receiveAsFlow()

    val receivedPacketsChannel : Channel<DataPacketVariant> = Channel(capacity = UNLIMITED)
    protected val receivedPacketsFlow: Flow<DataPacketVariant> = receivedPacketsChannel.receiveAsFlow()

    protected var currentJob: Job? = null

    protected fun updateOtherUserInfo(userInfo: UserInfo) {
        _transactionDataBuffer.update {
            it.copy(otherUserInfo = userInfo)
        }
    }

    suspend fun sendUserInfo(userInfo: UserInfo){
        _outputDataPacketChannel.send(userInfo)
    }

    protected suspend fun sendPositiveResult(){
        _outputDataPacketChannel.send(TransactionResult(ResultType.Success))

    }

    protected suspend fun sendNegativeResult(message: String? = null){
        _outputDataPacketChannel.send(TransactionResult(ResultType.Failure(message)))
    }

    fun resetTransaction() {
        resetJob()
        _transactionDataBuffer.update { TransactionDataBuffer() }
        //todo something like this to reset all channels?
//        receivedPacketsChannel.cancel()

    }
    private fun resetJob() {
        currentJob?.cancel()
        currentJob = null
    }

    class InvalidStepsOrderException(
        lastStep: TransactionSteps,
        attemptedStep: TransactionSteps
    ) : Exception(
        """Invalid step order. 
        |Last step was: $lastStep
        |Tried to do step: $attemptedStep""".trimMargin()
    )

    class WrongPacketTypeReceived(
        message: String? = null,
        expectedPacketType: DataPacketType? = null,
        receivedPacketType: DataPacketType? = null
    ) : Exception(
        message ?: """Unexpected packet type received. 
        |Expected packet type: $expectedPacketType 
        |Received packet type: $receivedPacketType""".trimMargin()
    )
}