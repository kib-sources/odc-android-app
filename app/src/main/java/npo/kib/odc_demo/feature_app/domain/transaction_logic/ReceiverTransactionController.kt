package npo.kib.odc_demo.feature_app.domain.transaction_logic

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithBlockchain
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.DataPacketType.*
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.*
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionStatus.*
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionStatus.ReceiverTransactionStatus.*
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForReceiver
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForReceiver.*
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForReceiver.WAITING_FOR_SIGNED_BLOCK

class ReceiverTransactionController(
    walletRepository: WalletRepository,
    scope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : TransactionController(
    scope = scope,
    walletRepository = walletRepository,
    role = TransactionRole.RECEIVER
) {
    private val _currentStep = MutableStateFlow(WAITING_FOR_AMOUNT_REQUEST)
    private val currentStep = _currentStep.asStateFlow()
    private fun updateStep(step: ForReceiver) {
        _currentStep.value = step
    }

    private val _transactionStatus: MutableStateFlow<ReceiverTransactionStatus> =
        MutableStateFlow(WAITING_FOR_OFFER)
    val transactionStatus = _transactionStatus.asStateFlow()
    private fun updateStatus(newStatus: ReceiverTransactionStatus) {
        _transactionStatus.value = newStatus
    }

    /**Called by [TransactionController] when a [TransactionException][TransactionController.TransactionException]
     * is caught by the [receivedPacketsFlow].catch{ } inside the [startProcessingIncomingPackets()][TransactionController.startProcessingIncomingPackets]  */
    override fun onTransactionError() {
        updateStatus(ERROR)
        updateStep(FINISHED)
    }

    init {
        initController()
    }

    //fixme probably should be used in a single place (entrypoint)
    // like in the startProcessingIncomingPackets() and be private
    public override fun initController(): Boolean {
        val started = super.initController()
        if (started) {
            updateStep(WAITING_FOR_AMOUNT_REQUEST)
            updateStatus(WAITING_FOR_OFFER)
        }
        return started
    }

    override suspend fun processPacketOnCurrentStep(packet: DataPacketVariant) {
        // user info can be processed at any moment now
        if (packet.packetType == USER_INFO) {
            updateOtherUserInfo(packet as UserInfo)
        } else when (currentStep.value) {
            WAITING_FOR_AMOUNT_REQUEST -> {
                //we are on this step initially or when have rejected the offer
                packet.requireToBeOfTypes(AMOUNT_REQUEST)
                try {
                    (packet as AmountRequest).isValid()
                    updateAmountRequest(amountRequest = packet)
                    updateStatus(OFFER_RECEIVED)
                    updateStep(AMOUNT_REQUEST_RECEIVED)
                } catch (e: TransactionException) {
                    sendAmountRequestRejection(e.message)
                }
            }
            AMOUNT_REQUEST_RECEIVED -> {
                packet.requireToBeOfTypes()
                //should not receive anything until reacting to the offer (except userInfo that can be received at any moment now)
                //when have reacted to the offer, the step will be switched manually from the UI to the next one or back to the previous one
                //if the amount request is invalid, send rejection immediately

            }
            WAITING_FOR_BANKNOTES_LIST -> {
                //we are on this step when have accepted the offer
                packet.requireToBeOfTypes(BANKNOTES_LIST)
                onReceivedBanknotesList(packet as BanknotesList)
            }
            WAITING_FOR_SIGNED_BLOCK -> {
                packet.requireToBeOfTypes(SIGNED_BLOCK)
                verifyReceivedBlock(packet as Block)
            }
            WAITING_FOR_RESULT -> {
                packet.requireToBeOfTypes(TRANSACTION_RESULT)
                val result = packet as TransactionResult
                updateReceivedTransactionResult(result)
                //if all banknotes are processed then the result should indicate that the other side received our positive result
                //then we can save banknotes and the other side will delete the corresponding ones from their wallet
                if (transactionDataBuffer.value.allBanknotesProcessed) {
                    if (result.value is TransactionResult.ResultType.Success) {
                        saveBanknotesToWallet()
                        updateStep(FINISHED)
                        updateStatus(FINISHED_SUCCESSFULLY)
                    } else throw transactionExceptionWithRole("The other side did not confirm that all the banknotes were processed.")
                } else throw transactionExceptionWithRole(
                    """On WAITING_FOR_RESULT step, received result but no conditions were satisfied.
                        |Assuming a transaction error occurred.""".trimMargin()
                )
                //todo check for results on other steps if needed
            }
            FINISHED -> {
//                nothing is expected after the transaction has finished
            }
        }
    }

    /** Will be called when on the [AMOUNT_REQUEST_RECEIVED] step to reject the offer.
    Going back to [WAITING_FOR_AMOUNT_REQUEST] step so as to be able to receive another offer*/
    suspend fun sendAmountRequestRejection(cause: String? = null) {
        if (currentStep.value == AMOUNT_REQUEST_RECEIVED) {
            sendNegativeResult(cause)
            updateAmountRequest(null)
            updateStep(WAITING_FOR_AMOUNT_REQUEST)
            updateStatus(WAITING_FOR_OFFER)
        } else throw transactionExceptionWithRole(
            "Tried sending OFFER REJECTION when not on AMOUNT_REQUEST_RECEIVED step"
        )
    }

    suspend fun sendAmountRequestApproval() {
        if (currentStep.value == AMOUNT_REQUEST_RECEIVED) {
            sendPositiveResult()
            updateStep(WAITING_FOR_BANKNOTES_LIST)
            updateStatus(RECEIVING_BANKNOTES_LIST)
        } else throw transactionExceptionWithRole(
            "Tried sending OFFER APPROVAL when not on AMOUNT_REQUEST_RECEIVED step"
        )
    }

    private suspend fun onReceivedBanknotesList(banknotesList: BanknotesList) {
        updateStatus(BANKNOTES_LIST_RECEIVED)
        if (banknotesList.list.isEmpty()) {
            sendNegativeResult("Received empty banknotes list")
            //if catch block in processPackets catches the below exception and sends the message,
            // the above may not be needed and the throw statement below is enough
            throw transactionExceptionWithRole(message = "Received banknotesList is empty!")
        }
        //this banknotes list contains banknotes with partly filled protectedBlock's
        //Final banknotes to be saved, with verified new signed blocks and
        // new protected blocks (created on this side),
        //will be saved separately in the finalBanknotesToDB property.
        updateBanknotesList(banknotesList)
        //confirm that banknotes are received
        sendPositiveResult()
        //start processing the first banknote
        createAcceptanceBlocksAndSend()
    }


    private suspend fun createAcceptanceBlocksAndSend() {
        updateStatus(CREATING_SENDING_ACCEPTANCE_BLOCKS)
        val acceptanceBlocks = withContext(defaultDispatcher) {
            val banknoteIndex = transactionDataBuffer.value.currentlyProcessedBanknoteOrdinal
            val currentProcessedBanknote =
                transactionDataBuffer.value.banknotesList!!.list[banknoteIndex]
            //new childBlock. New protected block from the old partly filled protected block's data.
            walletRepository.walletAcceptanceInit(
                currentProcessedBanknote.blocks,
                currentProcessedBanknote.banknoteWithProtectedBlock.protectedBlock
            )
        }
        updateLastAcceptanceBLocks(acceptanceBlocks)
        outputDataPacketChannel.send(acceptanceBlocks)
        updateStep(WAITING_FOR_SIGNED_BLOCK)
        updateStatus(ReceiverTransactionStatus.WAITING_FOR_SIGNED_BLOCK)
    }

    // TODO verification disabled for demo (?)
    private suspend fun verifyReceivedBlock(block: Block) {
        updateStatus(VERIFYING_RECEIVED_BLOCK)
        updateLastSignedBLock(block)
        withContext(defaultDispatcher) {
            val currentProcessedBanknote = currentProcessedBanknote!!
            // TODO verification disabled for demo
//        if (!block.verification(current_banknote_blocks.last().otok)) {
//            sendNegativeResult("received block is incorrectly signed")
//            throw Exception("received block is incorrectly signed")
//        }
            //if verification is successful, add new block to the blockchain
            val resultBlocks = currentProcessedBanknote.blocks + block
            //Previously in AcceptanceBlocks we sent a new unsigned block and a new protected block for current banknote
            //but they do not return the protected block back, so we've saved it in lastAcceptanceBlocks.
            //Since inside walletSignature() they have also verified this new protected block, we assume
            //that what we've sent was correct, because we've received a new signed block and not a failure result.
            val lastSentNewProtectedBlock =
                transactionDataBuffer.value.lastAcceptanceBlocks!!.protectedBlock
            val resultBanknote = BanknoteWithBlockchain(
                currentProcessedBanknote.banknoteWithProtectedBlock.copy(
                    protectedBlock = lastSentNewProtectedBlock
                ),
                resultBlocks
            )
            _transactionDataBuffer.update {
                it.copy(
                    finalBanknotesToDB = it.finalBanknotesToDB + resultBanknote,
                    currentlyProcessedBanknoteOrdinal = it.currentlyProcessedBanknoteOrdinal + 1
                )
            }
        }
        //start processing next banknote if not all banknotes are processed
        if (currentBanknoteOrdinal < banknotesList!!.size) createAcceptanceBlocksAndSend()
        else {
            _transactionDataBuffer.update { it.copy(allBanknotesProcessed = true) }
            updateStatus(ALL_BANKNOTES_VERIFIED)
            //notify that all the banknotes were successfully processed
            sendPositiveResult()
            //wait for confirmation of reception from the other side
            updateStep(WAITING_FOR_RESULT)
        }
    }


    private suspend fun saveBanknotesToWallet() {
        withContext(NonCancellable) {
            updateStatus(SAVING_BANKNOTES_TO_WALLET)
            walletRepository.addBanknotesToWallet(transactionDataBuffer.value.finalBanknotesToDB)
            updateStatus(BANKNOTES_SAVED)
        }
    }


    @Throws(TransactionException::class)
    private fun AmountRequest.isValid() {
        when {
            amount <= 0 -> throw transactionExceptionWithRole(
                "Received an invalid amount request, amount is <= 0 "
            )
            walletId.isBlank() -> throw transactionExceptionWithRole(
                "The wid in the amount request was empty or blank"
            )
            walletId != transactionDataBuffer.value.otherUserInfo?.walletId -> throw transactionExceptionWithRole(
                """The wid in the amount request was different from your saved UserInfo wid. 
                    |Request wid: $walletId .
                    |Saved UserInfo wid: ${transactionDataBuffer.value.otherUserInfo?.walletId}""".trimMargin()
            )
        }
    }
}