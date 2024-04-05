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
    protected val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    protected val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    protected val walletRepository: WalletRepository,
    protected val role: TransactionRole
) {

    private val exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        val exceptionText = "${e::class.simpleName}: ${e.message}"
        _transactionDataBuffer.update { it.copy(lastException = exceptionText) }
        CoroutineScope(ioDispatcher).launch { _errors.emit(exceptionText) }
        onTransactionError()
    }

    private val scope = CoroutineScope(defaultDispatcher + exceptionHandler)

    protected val _transactionDataBuffer: MutableStateFlow<TransactionDataBuffer> =
        MutableStateFlow(TransactionDataBuffer())
    val transactionDataBuffer = _transactionDataBuffer.asStateFlow()

    //todo maybe only create errors flow outside controllers
    // and catch TransactionException
    private val _errors = MutableSharedFlow<String>(extraBufferCapacity = 10)
    val errors: SharedFlow<String> = _errors.asSharedFlow()
    protected lateinit var outputDataPacketChannel: Channel<DataPacketVariant>
        private set
    val outputDataPacketFlow: Flow<DataPacketVariant>
        get() = outputDataPacketChannel.consumeAsFlow() //todo fix calls outside. java.lang.IllegalStateException: ReceiveChannel.consumeAsFlow can be collected just once

    lateinit var receivedPacketsChannel: Channel<DataPacketVariant>
        private set
    protected val receivedPacketsFlow: Flow<DataPacketVariant>
        get() = receivedPacketsChannel.consumeAsFlow()

    protected var currentBanknoteIndex: Int
        get() = transactionDataBuffer.value.currentlyProcessedBanknoteIndex
        set(value) = _transactionDataBuffer.update {
            it.copy(currentlyProcessedBanknoteIndex = value)
        }
    protected val currentProcessedBanknote: BanknoteWithBlockchain
        get() = transactionDataBuffer.value.banknotesList?.list?.get(currentBanknoteIndex) ?: throw transactionExceptionWithRole("currentProcessedBanknote getter" +
                " had null in ...banknotesList?.list?...")
    protected val banknotesList: List<BanknoteWithBlockchain>
        get() = transactionDataBuffer.value.banknotesList?.list ?: throw transactionExceptionWithRole("banknotesList getter had null in banknotesList?.list?")

    private var started: Boolean = false

    protected abstract fun onTransactionError()

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
        }.flowOn(ioDispatcher).launchIn(scope)
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

    protected fun transactionExceptionWithRole(message: String? = null) =
        TransactionException("${role.name}: ${message.orEmpty()}")
}