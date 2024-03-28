package npo.kib.odc_demo.feature_app.domain.transaction_logic

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.*
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithBlockchain
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.DataPacketType
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.*
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.TransactionResult.ResultType
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.util.cancelChildren

abstract class TransactionController(
    protected val scope: CoroutineScope,
    protected val walletRepository: WalletRepository,
    protected val role: TransactionRole
) {
    protected val _transactionDataBuffer: MutableStateFlow<TransactionDataBuffer> =
        MutableStateFlow(TransactionDataBuffer())
    val transactionDataBuffer = _transactionDataBuffer.asStateFlow()

    protected abstract fun onTransactionError()

    //todo maybe only create errors flow outside controllers
    // and catch TransactionException
    protected val _errors = MutableSharedFlow<String>(extraBufferCapacity = 10)
    val errors: SharedFlow<String> = _errors.asSharedFlow()
    protected lateinit var outputDataPacketChannel: Channel<DataPacketVariant>
        private set
    val outputDataPacketFlow: Flow<DataPacketVariant>
        get() = outputDataPacketChannel.receiveAsFlow() //todo fix calls outside. java.lang.IllegalStateException: ReceiveChannel.consumeAsFlow can be collected just once

    lateinit var receivedPacketsChannel: Channel<DataPacketVariant>
        private set
    protected val receivedPacketsFlow: Flow<DataPacketVariant>
        get() = receivedPacketsChannel.receiveAsFlow()

    protected var currentBanknoteOrdinal: Int
        get() = transactionDataBuffer.value.currentlyProcessedBanknoteOrdinal
        set(value) = _transactionDataBuffer.update {
            it.copy(currentlyProcessedBanknoteOrdinal = value)
        }
    protected val currentProcessedBanknote: BanknoteWithBlockchain?
        get() = transactionDataBuffer.value.banknotesList?.list?.get(currentBanknoteOrdinal)
    protected val banknotesList: List<BanknoteWithBlockchain>?
        get() = transactionDataBuffer.value.banknotesList?.list

    protected var started: Boolean = false
        private set

    /** Initializes controller
     * @return **false** if already started, else **true** */
    protected open fun initController(): Boolean {
        return if (!started) {
            receivedPacketsChannel = Channel(capacity = UNLIMITED)
            outputDataPacketChannel = Channel(capacity = UNLIMITED)
            //instantly upon the initialization UserInfo is added to the queue as the first packet to be sent
            started = true
            updateLocalUserInfo()
            true
        } else false
    }

    fun resetController(): Boolean {
        return if (started) {
            //reset and clear all channels with .cancel() and to restart assign new channel instances to properties
            // old channels with no references will be GC'd
            receivedPacketsChannel.cancel()
            outputDataPacketChannel.cancel()
            _transactionDataBuffer.update { TransactionDataBuffer() }
            scope.cancelChildren()
            started = false
            true
        } else false
    }

    /**
     *  The entrypoint to the transactionController processing.
     *  Upon completion the [resetController] is invoked.
     * */
    fun startProcessingIncomingPackets() {
        receivedPacketsFlow.onEach { packet ->
            processPacketOnCurrentStep(packet)
        }.catch { e ->
            withContext(NonCancellable) {
                if (e is TransactionException) {
                    _transactionDataBuffer.update { it.copy(lastException = "${e::class.simpleName}: ${e.message}") }
                    onTransactionError()
                    //todo maybe create a new ERROR packet variant and send it instead?
//                    repeat(2) { sendNegativeResult("A TRANSACTION ERROR HAS OCCURRED ON THE ${role.name} SIDE") } // seems to be crashing the app
                    throw CancellationException("Cancelled due to a transaction exception caught")
                }
            }
        }.onCompletion {
            withContext(NonCancellable) {
                delay(5000)
                resetController()
            }
        }.launchIn(scope)
    }

    protected abstract suspend fun processPacketOnCurrentStep(packet: DataPacketVariant)

    protected fun updateOtherUserInfo(userInfo: UserInfo) {
        if (started) _transactionDataBuffer.update {
            it.copy(otherUserInfo = userInfo)
        }
    }

    fun updateLocalUserInfo() {
        if (started) scope.launch {
            val userInfo = walletRepository.getLocalUserInfo()
            _transactionDataBuffer.update { it.copy(thisUserInfo = userInfo) }
            outputDataPacketChannel.send(userInfo)
        }
    }

    protected suspend fun sendPositiveResult() {
        if (started) outputDataPacketChannel.send(TransactionResult(ResultType.Success))
    }

    protected suspend fun sendNegativeResult(message: String? = null) {
        if (started) outputDataPacketChannel.send(TransactionResult(ResultType.Failure(message)))
    }

    protected fun updateReceivedTransactionResult(result: TransactionResult) {
        if (started) _transactionDataBuffer.update { it.copy(transactionResult = result) }

    }

    protected fun updateAmountRequest(amountRequest: AmountRequest?) {
        if (started) _transactionDataBuffer.update { it.copy(amountRequest = amountRequest) }
    }

    protected fun updateBanknotesList(list: BanknotesList) {
        if (started) _transactionDataBuffer.update { it.copy(banknotesList = list) }
    }

    protected fun updateLastAcceptanceBLocks(blocks: AcceptanceBlocks) {
        if (started) _transactionDataBuffer.update { it.copy(lastAcceptanceBlocks = blocks) }
    }

    protected fun updateLastSignedBLock(block: Block) {
        if (started) _transactionDataBuffer.update { it.copy(lastSignedBlock = block) }
    }


    protected fun DataPacketVariant.requireToBeOfTypes(vararg expectedTypes: DataPacketType) {
        if (!expectedTypes.contains(packetType)) {
            if (this is TransactionResult && this.value is ResultType.Failure) throw transactionExceptionWithRole(
                """Received an unexpected failure packet.
                | It may signal that an error has happened on the other side.
                | Received failure message:
                | ${this.value.message}""".trimMargin()
            )
            else throw transactionExceptionWithRole(
                if (expectedTypes.isEmpty()) "Expected no packets right now but received $packetType"
                else "Expected packet in ${expectedTypes.contentToString()} but received $packetType"
            )
        }
    }

    //todo for some reason with a protected constructor would not be visible in subclasses of TransactionController
    protected class TransactionException(message: String? = null) : Exception(message)
    //todo maybe makes sense to create an Error DataPacketVariant to distinguish
    // exceptions from this side or from the other side. For the received exceptions
    // it probably makes sense not to send anything back.
    protected fun transactionExceptionWithRole(message: String? = null) =
        TransactionException("${role.name}: ${message.orEmpty()}")
}