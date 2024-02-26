package npo.kib.odc_demo.feature_app.domain.transaction_logic

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithBlockchain
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.DataPacketType.*
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.*
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForSender
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForSender.*
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.TransactionRole.SENDER
import npo.kib.odc_demo.feature_app.domain.transaction_logic.util.findBanknotesWithSum


class SenderTransactionController(
    walletRepository: WalletRepository,
    scope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : TransactionController(
    scope = scope, walletRepository = walletRepository, role = SENDER
) {

    private val _currentStep: MutableStateFlow<ForSender> = MutableStateFlow(INITIAL)
    override val currentStep: StateFlow<ForSender> = _currentStep.asStateFlow()

    init {
        initController()
    }

    public override fun initController(): Boolean {
        val result = super.initController()
        if (result) updateStep(INITIAL)
        return result
    }

    fun startProcessingIncomingPackets() {
        receivedPacketsFlow.onEach { packet ->
            processPacketOnCurrentStep(packet)
        }.onCompletion { withContext(NonCancellable) { resetController() } }.launchIn(scope)
    }

    private suspend fun processPacketOnCurrentStep(packet: DataPacketVariant) {
        // user info can be processed at any moment now
        if (packet.packetType == USER_INFO) {
            updateOtherUserInfo(packet as UserInfo)
        } else when (currentStep.value) {
            INITIAL -> {
                //nothing is expected to be received here yet
                //initial step where we can try to construct amount and then send it
                //when we send it (trigger manually through UI), the step is set to WAITING_FOR_OFFER_RESPONSE
            }
            WAITING_FOR_OFFER_RESPONSE -> {
                packet.requireToBeOfTypes(TRANSACTION_RESULT)
                when ((packet as TransactionResult).value) {
                    TransactionResult.ResultType.Success -> {
                        //when accepted, send BanknotesList
                        sendBanknotesList()
                    }
                    is TransactionResult.ResultType.Failure -> {
                        //when rejected go back to initial and be able to send offer again or
                        // try to construct another amount
                        updateStep(
                            INITIAL
                        ) //todo can also emit result to errors or just by default just show that the offer was rejected without cause
                    }
                }
            }
            WAITING_FOR_BANKNOTES_LIST_RECEIVED_RESPONSE -> {
                packet.requireToBeOfTypes(TRANSACTION_RESULT)
                when ((packet as TransactionResult).value) {
                    TransactionResult.ResultType.Success -> {
                        updateStep(WAITING_FOR_ACCEPTANCE_BLOCKS)
                    }
                    is TransactionResult.ResultType.Failure -> {
                        //If an invalid BanknotesList was sent (empty, etc. Practically an exception). Can emit result message to errors
                        updateStep(INITIAL)
                    }
                }
            }
            WAITING_FOR_ACCEPTANCE_BLOCKS -> {
                packet.requireToBeOfTypes(ACCEPTANCE_BLOCKS)
                onAcceptanceBlocksReceived(packet as AcceptanceBlocks)
            }
            WAITING_FOR_RESULT -> {
                packet.requireToBeOfTypes(TRANSACTION_RESULT)
                val result = (packet as TransactionResult).value
                if (transactionDataBuffer.value.allBanknotesProcessed) {
                    if (result is TransactionResult.ResultType.Success) {
                        deleteLocalBanknotes()
                        withContext(NonCancellable) {
                            sendPositiveResult()
                        }
                        updateStep(FINISHED)
                    } else throw TransactionException("on WAITING_FOR_RESULT step, received result but no conditions were satisfied")
                }
            }
            FINISHED -> {

            }
        }
    }

    suspend fun tryConstructAmount(amount: Int): Boolean {
        return withContext(defaultDispatcher) {
            if (currentStep.value == INITIAL) {
                _transactionDataBuffer.update { it.copy(isAmountAvailable = null) }
                //have to also partially clear protected blocks of banknotes before sending for some reason
                // (most likely to reduce size, but also maybe for security)
                val resultBanknotes: List<BanknoteWithBlockchain>? =
                    getBanknotesFromAmount(amount)?.map {
                        ensureActive()
                        val newProtectedBlock = walletRepository.walletInitProtectedBlock(
                            it.banknoteWithProtectedBlock.protectedBlock
                        )
                        it.copy(
                            banknoteWithProtectedBlock = it.banknoteWithProtectedBlock.copy(
                                protectedBlock = newProtectedBlock
                            )
                        )
                    }
                return@withContext if (resultBanknotes == null) {
                    _transactionDataBuffer.update { it.copy(isAmountAvailable = false) }
                    false
                } else {
                    _transactionDataBuffer.update {
                        it.copy(
                            isAmountAvailable = true,
                            banknotesList = BanknotesList(resultBanknotes),
                            amountRequest = AmountRequest(
                                amount = amount,
                                walletId = walletRepository.getOrRegisterWallet().walletId
                            )
                        )
                    }
                    true
                }
            } else return@withContext false
        }
    }

    suspend fun trySendOffer() {
        when {
            currentStep.value != INITIAL -> throw TransactionException(
                "Tried sending offer while not on INITIAL step"
            )  //todo emit to errors (?)
            transactionDataBuffer.value.isAmountAvailable == true -> {
                updateStep(WAITING_FOR_OFFER_RESPONSE)
                outputDataPacketChannel.send(transactionDataBuffer.value.amountRequest!!)
            }
            else -> throw TransactionException(
                "Cannot send the offer, the amount is not available in transactionDataBuffer"
            )
        }
    }

    private suspend fun sendBanknotesList() {
        val list = transactionDataBuffer.value.banknotesList!!.also {
            if (it.list.isEmpty()) throw TransactionException(
                "Tried sending banknotes list but it is empty in transactionDataBuffer"
            )
            //todo emit to errors (?)
        }
        outputDataPacketChannel.send(list)
        updateStep(WAITING_FOR_BANKNOTES_LIST_RECEIVED_RESPONSE)
    }

    private suspend fun onAcceptanceBlocksReceived(acceptanceBlocks: AcceptanceBlocks) {
        updateLastAcceptanceBLocks(acceptanceBlocks)
        withContext(defaultDispatcher) {
            val currentBanknoteLastBlock = currentProcessedBanknote!!.blocks.last()
            val resultBlock = walletRepository.walletSignature(
                parentBlock = currentBanknoteLastBlock, childBlock = acceptanceBlocks.childBlock,
                protectedBlock = acceptanceBlocks.protectedBlock
            )
            outputDataPacketChannel.send(resultBlock)
            currentBanknoteOrdinal++
            if (currentBanknoteOrdinal >= banknotesList!!.size) {
                _transactionDataBuffer.update { it.copy(allBanknotesProcessed = true) }
                updateStep(WAITING_FOR_RESULT)
            }
        }
//        outputDataPacketChannel.send()
    }

    private suspend fun deleteLocalBanknotes() {
        withContext(NonCancellable) {
            val bnidList =
                transactionDataBuffer.value.banknotesList?.list?.map { it.banknoteWithProtectedBlock.banknote.bnid }
            if (bnidList == null) throw TransactionException(
                "BanknotesList in buffer is null"
            ) else if (bnidList.isEmpty()) throw TransactionException(
                "BanknotesList in buffer is empty"
            ) else walletRepository.deleteBanknotesWithBlockchainByBnids(bnidList)
        }
    }

    private fun updateStep(step: ForSender) {
        _currentStep.value = step
    }

    /**
     *  Subset Sum Problem, NP-Hard
     *  @see <a href="https://en.wikipedia.org/wiki/Subset_sum_problem">Subset Sum Problem - Wikipedia</a>
     * */
    private suspend fun getBanknotesFromAmount(amount: Int): List<BanknoteWithBlockchain>? {
        val allAmounts = withContext(ioDispatcher) { walletRepository.getBanknotesIdsAndAmounts() }
        return withContext(defaultDispatcher) {
            val resultAmounts = findBanknotesWithSum(
                banknotesIdsAmounts = allAmounts, targetSum = amount
            ) ?: return@withContext null
            val resultBnids = resultAmounts.map { ensureActive(); it.bnid }
            return@withContext walletRepository.getBanknotesWithBlockchainByBnids(resultBnids)
        }
    }
}