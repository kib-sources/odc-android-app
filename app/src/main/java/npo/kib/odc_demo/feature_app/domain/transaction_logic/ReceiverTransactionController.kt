package npo.kib.odc_demo.feature_app.domain.transaction_logic

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithBlockchain
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.DataPacketType
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.DataPacketType.*
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.*
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.TransactionResult.ResultType
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForReceiver
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForReceiver.*
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForReceiver2
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.TransactionRole.Receiver

class ReceiverTransactionController(
    private val walletRepository: WalletRepository,
    private val externalScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : TransactionController(externalScope = externalScope, role = Receiver) {

    private val _currentStep = MutableStateFlow(ForReceiver2.WAITING_FOR_OFFER)
    override val currentStep = _currentStep.asStateFlow()


    private val _awaitedPacketType: MutableStateFlow<DataPacketType> =
        MutableStateFlow(AMOUNT_REQUEST)
    val awaitedPacketType: StateFlow<DataPacketType> = _awaitedPacketType.asStateFlow()

    init {
        initController()
        //instantly after the connection the first thing to be queued for sending is UserInfo
        externalScope.launch {
            val userInfo = walletRepository.getLocalUserInfo()
            updateLocalUserInfo(userInfo)
            outputDataPacketChannel.send(userInfo)
        }
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
            processDataPacket(packet)
        }.launchIn(externalScope)
    }

    fun startProcessingIncomingPackets2() {
        receivedPacketsFlow.onEach { packet ->
            processDataPacket2(packet)
        }.launchIn(externalScope)
    }

    private suspend fun processDataPacket2(packet: DataPacketVariant) {
        //UserInfo packet can be processed at any moment now
        if (packet.packetType == USER_INFO) updateOtherUserInfo(packet as UserInfo)
        else if (packet.packetType != awaitedPacketType.value) {
            throw WrongPacketTypeReceived(
                expectedPacketType = awaitedPacketType.value, receivedPacketType = packet.packetType
            )
        } else when (currentStep.value) {
//            ForReceiver2.STOPPED -> {/* shouldn't receive packets at this point yet */}
            ForReceiver2.WAITING_FOR_OFFER -> {

            }

            ForReceiver2.WAITING_FOR_BANKNOTES -> {

            }

            ForReceiver2.INIT_VERIFICATION -> TODO()
            ForReceiver2.SENDING_ACCEPTANCE_BLOCKS -> TODO()
            ForReceiver2.VERIFYING_SIGNATURE -> TODO()
            ForReceiver2.SAVING_BANKNOTES_TO_WALLET -> TODO()
        }
    }

    @Throws(WrongPacketTypeReceived::class)
    private suspend fun processDataPacket(packet: DataPacketVariant) {
        //UserInfo packet can be processed at any moment now
        if (packet.packetType == awaitedPacketType.value || packet.packetType == USER_INFO) when (packet.packetType) {
            USER_INFO -> updateOtherUserInfo(packet as UserInfo)
            AMOUNT_REQUEST -> updateAmountRequest(packet as AmountRequest)
            BANKNOTES_LIST -> onReceivedBanknotesList(packet as BanknotesList)
            SIGNED_BLOCK -> verifyReceivedBlock(packet as Block)
            ACCEPTANCE_BLOCKS -> throw WrongPacketTypeReceived(
                message = "Receiving side can not receive ACCEPTANCE_BLOCKS"
            )

            RESULT -> {
                //todo actually can wait for result and only then move to the next step (return transaction steps)
                // but for now don't need to receive results, only data, and then send results back
            }
        } else {
            throw WrongPacketTypeReceived(
                expectedPacketType = awaitedPacketType.value, receivedPacketType = packet.packetType
            )
        }


    }

    private fun startWaitingForOffer() {
        _awaitedPacketType.update { AMOUNT_REQUEST }
    }

    //Staying in WAIT_FOR_OFFER state when rejected so as to be able to receive another offer
    suspend fun sendOfferRejection() {
        sendNegativeResult("Offer rejected")
        updateAmountRequest(null)
    }

    suspend fun sendOfferApproval() {
        sendPositiveResult()
        _awaitedPacketType.update { BANKNOTES_LIST }
        _currentStep.value = ForReceiver2.WAITING_FOR_BANKNOTES
    }

    private suspend fun onReceivedBanknotesList(banknotesList: BanknotesList) {
        if (banknotesList.list.isEmpty()) {
            _errors.emit("Received empty banknotes list, terminating session in 1 s")
            sendNegativeResult("Received empty banknotes list, terminating session in 1 s")
            delay(1000)
            resetTransactionController()
            return
        }

        /*throw WrongPacketTypeReceived(message = "Received empty banknotes list")*/
        _transactionDataBuffer.update { it.copy(banknotesList = banknotesList) }
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
        outputDataPacketChannel.send(acceptanceBlocks)
        _awaitedPacketType.update { SIGNED_BLOCK }
    }

    // TODO verification disabled for demo (?)
    private suspend fun verifyReceivedBlock(block: Block) {
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
            sendPositiveResult()
            saveBanknotesToWallet()
        }
    }

    private suspend fun saveBanknotesToWallet() {
        walletRepository.addBanknotesToWallet(transactionDataBuffer.value.finalBanknotesToDB)
    }

    private fun processAmountRequest(amountRequest: AmountRequest) {
        if (amountRequest.amount <= 0) {
            //Invalid amount requested, may throw an exception
        } /*else if (amountRequest.walletId != transactionDataBuffer.value.otherUserInfo?.walletId ?: true) {
            //As an additional check, may throw an exception if request wid is not equal to otherUser wid or if either is null
        }*/
        else {
            
        }
    }

    private fun updateAmountRequest(amountRequest: AmountRequest?) {
        _transactionDataBuffer.update { it.copy(amountRequest = amountRequest) }
    }


    //todo transactionSteps should be a channel where pending steps are sent
    // then converted to a flow as usual which would be observed and a function onStepReceived() would be called
    // there wil be maaaaaany connections I assume, will be some complicated logic. But will be able to simplify
    // I am almost 100% certain, so should be no big deal
    private fun goNextStepOnResultReceived() {

    }

    /** This function is designed for a p2p receiver.
     *  Used to check if the new step can be invoked right after some other step.
     *  Think of it as of a finite-state machine connection validity checker.
     * @receiver new step to check
     * @param stepToFollow typically the current transaction step */
    private fun ForReceiver.canFollowStep(
        stepToFollow: ForReceiver
    ): Boolean = when (this) {
        INITIAL -> true
        WAIT_FOR_OFFER -> stepToFollow == INITIAL
        REJECT_OFFER -> stepToFollow == WAIT_FOR_OFFER
        ACCEPT_OFFER -> stepToFollow == WAIT_FOR_OFFER
        WAIT_FOR_BANKNOTES -> stepToFollow == ACCEPT_OFFER
        INIT_VERIFICATION -> stepToFollow == WAIT_FOR_BANKNOTES || stepToFollow == VERIFY_SIGNATURE
        SEND_ACCEPTANCE_BLOCKS -> stepToFollow == INIT_VERIFICATION
        VERIFY_SIGNATURE -> stepToFollow == SEND_ACCEPTANCE_BLOCKS
        SAVE_BANKNOTES_TO_WALLET -> stepToFollow == VERIFY_SIGNATURE
        //right now is used only for sending transaction result at the end after saving all banknotes to wallet
        //todo later add possibility to send result after any transaction step (like after receiving all banknotes and verifying block signature)
        SEND_RESULT -> stepToFollow == SAVE_BANKNOTES_TO_WALLET
    }


    fun updateLocalUserInfo(userInfo: UserInfo) =
        _transactionDataBuffer.update { it.copy(thisUserInfo = userInfo) }
}

