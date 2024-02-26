package npo.kib.odc_demo.feature_app.domain.transaction_logic

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithBlockchain
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.DataPacketType.*
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.*
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForReceiver
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForReceiver.*
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.TransactionRole.RECEIVER

class ReceiverTransactionController(
    walletRepository: WalletRepository,
    scope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : TransactionController(
    scope = scope, walletRepository = walletRepository, role = RECEIVER
) {

    //todo this controller state could be used to partially signal changes to UI
    // (along with bluetoothControllerState)
//    companion object {
//        enum class ReceiverTransactionState {
//            INITIAL, ERROR
//        }
//    }

    private val _currentStep = MutableStateFlow(WAITING_FOR_AMOUNT_REQUEST)
    override val currentStep = _currentStep.asStateFlow()

    init {
        initController()
    }

    //fixme probably should be used in a single place (entrypoint)
    // like in the startProcessingIncomingPackets() and be private
    public override fun initController(): Boolean {
        val result = super.initController()
        if (result) updateStep(WAITING_FOR_AMOUNT_REQUEST)
        return result
    }

    fun startProcessingIncomingPackets() {
        receivedPacketsFlow.onEach { packet ->
            processPacketOnCurrentStep(packet)
        }.onCompletion { withContext(NonCancellable) { resetController() } }.launchIn(scope)
    }


    //todo maybe send out all small events in a flow, "initializing verification", etc?
    // use ReceiverTransactionState ...?
    private suspend fun processPacketOnCurrentStep(packet: DataPacketVariant) {
        // user info can be processed at any moment now
        if (packet.packetType == USER_INFO) {
            updateOtherUserInfo(packet as UserInfo)
        } else when (currentStep.value) {
            WAITING_FOR_AMOUNT_REQUEST -> {
                //we are on this step initially or when have rejected the offer
                packet.requireToBeOfTypes(AMOUNT_REQUEST)
                updateAmountRequest(amountRequest = packet as AmountRequest)
                updateStep(AMOUNT_REQUEST_RECEIVED)
            }
            AMOUNT_REQUEST_RECEIVED -> {
                packet.requireToBeOfTypes() //should not receive anything until reacting to the offer (except userInfo that can be received at any moment now)
                //when have reacted to the offer, the step will be switched manually from the UI to the next one or back to the previous one
                //if the amount request is invalid, send rejection immediately
                try {
                    (packet as AmountRequest).isValid()
                } catch (e: WrongPacketTypeReceived) {
                    sendAmountRequestRejection(e.message)
                }
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
                val result = (packet as TransactionResult).value
                //if all banknotes are processed then the result should indicate that the other side received our positive result
                //then we can save banknotes and the other side will delete the corresponding ones from their wallet
                if (transactionDataBuffer.value.allBanknotesProcessed) {
                    if (result is TransactionResult.ResultType.Success) {
                        saveBanknotesToWallet()
                        updateStep(FINISHED)
                    }
                } else throw TransactionException("on WAITING_FOR_RESULT step, received result but no conditions were satisfied")
                //todo check for results on other steps if needed
            }
            FINISHED -> {
                //nothing is expected after the transaction has finished
//                packet.requireToBeOfTypes()
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
        } else throw TransactionException(
            "Tried sending OFFER REJECTION when not on AMOUNT_REQUEST_RECEIVED step"
        )
    }

    suspend fun sendAmountRequestApproval() {
        if (currentStep.value == AMOUNT_REQUEST_RECEIVED) {
            sendPositiveResult()
            updateStep(WAITING_FOR_BANKNOTES_LIST)
        } else throw TransactionException(
            "Tried sending OFFER APPROVAL when not on AMOUNT_REQUEST_RECEIVED step"
        )
    }

    private suspend fun onReceivedBanknotesList(banknotesList: BanknotesList) {
        if (banknotesList.list.isEmpty()) {
            val delay: Long = 1L
            _errors.emit("Received empty banknotes list, terminating session in $delay s")
            sendNegativeResult(
                "Received empty banknotes list, terminating session in $delay s"
            )/*throw WrongPacketTypeReceived(message = "Received empty banknotes list")*/
            delay(1000 * delay)
            resetController()
            return
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
        val banknoteIndex = transactionDataBuffer.value.currentlyProcessedBanknoteOrdinal
        val currentProcessedBanknote =
            transactionDataBuffer.value.banknotesList!!.list[banknoteIndex]
        //new childBlock. New protected block from the old partly filled protected block's data.
        val acceptanceBlocks = walletRepository.walletAcceptanceInit(
            currentProcessedBanknote.blocks,
            currentProcessedBanknote.banknoteWithProtectedBlock.protectedBlock
        )
        updateLastAcceptanceBLocks(acceptanceBlocks)
        outputDataPacketChannel.send(acceptanceBlocks)
        updateStep(WAITING_FOR_SIGNED_BLOCK)
    }

    // TODO verification disabled for demo (?)
    private suspend fun verifyReceivedBlock(block: Block) {
        updateLastSignedBLock(block)
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
            ), resultBlocks
        )
        _transactionDataBuffer.update {
            it.copy(
                finalBanknotesToDB = it.finalBanknotesToDB + resultBanknote,
                currentlyProcessedBanknoteOrdinal = it.currentlyProcessedBanknoteOrdinal + 1
            )
        }
        //start processing next banknote if not all banknotes are processed
        if (currentBanknoteOrdinal < banknotesList!!.size) createAcceptanceBlocksAndSend()
        else {
            _transactionDataBuffer.update { it.copy(allBanknotesProcessed = true) }
            //notify that all the banknotes were successfully processed
            sendPositiveResult()
            //wait for confirmation of reception from the other side
            updateStep(WAITING_FOR_RESULT)
        }
    }

    private suspend fun saveBanknotesToWallet() {
        withContext(NonCancellable) {
            walletRepository.addBanknotesToWallet(transactionDataBuffer.value.finalBanknotesToDB)
        }
    }


    private fun updateStep(step: ForReceiver) {
        _currentStep.value = step
    }

    @Throws(WrongPacketTypeReceived::class)
    private fun AmountRequest.isValid() {
        when {
            amount <= 0 -> throw WrongPacketTypeReceived(
                "Received an invalid amount request, amount is <= 0 "
            )
            walletId.isBlank() -> throw WrongPacketTypeReceived(
                "The wid in the amount request was empty or blank"
            )
            walletId != transactionDataBuffer.value.otherUserInfo?.walletId -> throw WrongPacketTypeReceived(
                """The wid in the amount request was different from your saved UserInfo wid. 
                    |Request wid: $walletId .
                    |Saved UserInfo wid: ${transactionDataBuffer.value.otherUserInfo?.walletId}""".trimMargin()
            )
        }
    }

}