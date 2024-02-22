package npo.kib.odc_demo.feature_app.domain.transaction_logic

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithBlockchain
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.DataPacketType.*
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.*
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForReceiver.*
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.TransactionRole.RECEIVER

class ReceiverTransactionController(
    walletRepository: WalletRepository,
    externalScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : TransactionController(
    externalScope = externalScope, walletRepository = walletRepository, role = RECEIVER
) {

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

    public override fun initController(): Boolean {
        val result = super.initController()
        _currentStep.value = WAITING_FOR_AMOUNT_REQUEST
        return result
    }

    /*    @Throws(InvalidStepsOrderException::class)
        suspend fun updateStep(step: ForReceiver) {
            if (step.canFollowStep(currentStep.value)) {
                when (step) {
                    INITIAL -> reset()
                    WAIT_FOR_OFFER -> listenForOffer()
                    REJECT_OFFER -> sendOfferRejection()
                    ACCEPT_OFFER -> sendOfferApproval() //or just replace with sending Result (SEND_RESULT)
                    WAIT_FOR_BANKNOTES -> listenForBanknotes() //send offer approval and wait for banknotes, can do in the same step
                    INIT_VERIFICATION -> initBanknoteVerification() //steps 2-4 todo split the steps' names and attach the appropriate name and documentation to each function
                    SEND_ACCEPTANCE_BLOCKS -> sendAcceptanceBlocks()
                    VERIFY_SIGNATURE -> verifyReceivedBlockSignature()
                    SAVE_BANKNOTES_TO_WALLET -> saveBanknotesToWallet()
                    SEND_RESULT -> sendResult()
                }
            }
            else throw InvalidStepsOrderException(
                lastStep = currentStep.value, attemptedStep = step
            )
        }*/

    fun startProcessingIncomingPackets() {
        receivedPacketsFlow.onEach { packet ->
            processPacketOnCurrentStep(packet)
        }.launchIn(externalScope)
    }


    //todo maybe send out events in a flow, about what happened, "initializing verification", etc?
    // use ReceiverTransactionState ...?
    private suspend fun processPacketOnCurrentStep(packet: DataPacketVariant) {
        if (packet.packetType == USER_INFO) {
            updateOtherUserInfo(packet as UserInfo)
        } else when (currentStep.value) {
            WAITING_FOR_AMOUNT_REQUEST -> {
                //we are on this step initially or when have rejected the offer
                packet.requireToBeOfTypes(AMOUNT_REQUEST)
                updateAmountRequest(amountRequest = packet as AmountRequest)
                _currentStep.value = AMOUNT_REQUEST_RECEIVED
            }

            AMOUNT_REQUEST_RECEIVED -> {
                packet.requireToBeOfTypes() //should not receive anything until reacting to the offer (except userInfo that can be received at any moment now)
                //when have reacted to the offer, the step will be switched manually from the UI to the next one or back to the previous one
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
                        _currentStep.value = FINISHED
                    }
                }
                //todo check for results on other steps if needed

            }

            FINISHED -> {
                //nothing is expected after the transaction has finished
//                packet.requireToBeOfTypes()
            }
        }
    }

    /** Will be called when on the [AMOUNT_REQUEST_RECEIVED] step to reject the offer.
    Staying in [WAITING_FOR_AMOUNT_REQUEST] state when rejected so as to be able to receive another offer*/
    suspend fun sendAmountRequestRejection() {
        sendNegativeResult("Offer rejected")
        updateAmountRequest(null)
        _currentStep.value = WAITING_FOR_AMOUNT_REQUEST
    }

    suspend fun sendAmountRequestApproval() {
        sendPositiveResult()
        _currentStep.value = WAITING_FOR_BANKNOTES_LIST
    }

    private suspend fun onReceivedBanknotesList(banknotesList: BanknotesList) {
        if (banknotesList.list.isEmpty()) {
            val delay: Long = 1L
            _errors.emit("Received empty banknotes list, terminating session in $delay s")
            sendNegativeResult(
                "Received empty banknotes list, terminating session in $delay s"
            )/*throw WrongPacketTypeReceived(message = "Received empty banknotes list")*/
            delay(1000 * delay)
            resetTransactionController()
            return
        }

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
        val acceptanceBlocks = walletRepository.walletAcceptanceInit(
            currentProcessedBanknote.blocks,
            currentProcessedBanknote.banknoteWithProtectedBlock.protectedBlock
        )
        updateLastAcceptanceBLocks(acceptanceBlocks)
        outputDataPacketChannel.send(acceptanceBlocks)
        _currentStep.value = WAITING_FOR_SIGNED_BLOCK
    }

    // TODO verification disabled for demo (?)
    private suspend fun verifyReceivedBlock(block: Block) {
        updateLastSignedBLock(block)
        val index = transactionDataBuffer.value.currentlyProcessedBanknoteOrdinal
        val banknotesList = transactionDataBuffer.value.banknotesList!!.list

        val currentProcessedBanknote = banknotesList[index]
        // TODO verification disabled for demo
//        if (!block.verification(current_banknote_blocks.last().otok)) {
//            sendNegativeResult("received block is incorrectly signed")
//            throw Exception("received block is incorrectly signed")
//        }
        //if verification is successful, add new block to the blockchain
        val resultBlocks = currentProcessedBanknote.blocks + block
        //an easy (but inefficient) way to deep copy banknoteWithProtectedBlock would be to serialize and deserialize it,
        //but there is no need to create a deep copy here
        val resultBanknote = BanknoteWithBlockchain(
            currentProcessedBanknote.banknoteWithProtectedBlock.copy(), resultBlocks
        )
        _transactionDataBuffer.update {
            it.copy(
                finalBanknotesToDB = it.finalBanknotesToDB + resultBanknote,
                currentlyProcessedBanknoteOrdinal = it.currentlyProcessedBanknoteOrdinal + 1
            )
        }
        //start processing next banknote if not all banknotes are processed
        if (index + 1 < banknotesList.size) createAcceptanceBlocksAndSend()
        else {
            _transactionDataBuffer.update { it.copy(allBanknotesProcessed = true) }
            //notify that all the banknotes were successfully processed
            sendPositiveResult()
            //wait for confirmation of reception from the other side
            _currentStep.value = WAITING_FOR_RESULT
        }
    }

    private suspend fun saveBanknotesToWallet() {
        walletRepository.addBanknotesToWallet(transactionDataBuffer.value.finalBanknotesToDB)
    }

    /*    private fun validateAmountRequest(amountRequest: AmountRequest) {
            if (amountRequest.amount <= 0) {
                throw WrongPacketTypeReceived("Received an invalid amount request, amount is <= 0 ")
            } else if (amountRequest.walletId != (transactionDataBuffer.value.otherUserInfo?.walletId
                    ?: true )
            ) {
                //As an additional check, may throw an exception if request wid is not equal to otherUser wid or if either is null
            }
            else {

            }
        }*/

}