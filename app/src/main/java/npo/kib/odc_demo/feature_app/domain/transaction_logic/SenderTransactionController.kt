package npo.kib.odc_demo.feature_app.domain.transaction_logic

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.yield
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithBlockchain
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.DataPacketType
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.DataPacketType.*
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.*
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.TransactionResult.ResultType.Success
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForSender
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForSender.*
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.TransactionRole.SENDER
import npo.kib.odc_demo.feature_app.domain.transaction_logic.util.findBanknotesWithSum


class SenderTransactionController(
    walletRepository: WalletRepository,
    externalScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : TransactionController(externalScope = externalScope, walletRepository = walletRepository, role = SENDER) {

//    private val _currentStep: Channel<ForSender> = Channel(UNLIMITED)
//    val currentStep: Flow<ForSender> = _currentStep.receiveAsFlow()

    private val _currentStep: MutableStateFlow<ForSender> = MutableStateFlow(INITIAL)
    override val currentStep: StateFlow<ForSender> = _currentStep.asStateFlow()

    private val _awaitedPacketType: MutableStateFlow<DataPacketType> = MutableStateFlow(USER_INFO)
    val awaitedPacketType: StateFlow<DataPacketType> = _awaitedPacketType.asStateFlow()

//     override val _errors = Channel<TransactionResult>(capacity = UNLIMITED)

    fun startProcessingIncomingPackets() {
        receivedPacketsFlow.onEach { packet ->
            processDataPacket(packet)
        }.launchIn(externalScope)
    }

    @Throws(WrongPacketTypeReceived::class)
    private suspend fun processDataPacket(packet: DataPacketVariant) {
        //UserInfo packet can be processed at any moment now
        if (packet.packetType != awaitedPacketType.value && packet.packetType != USER_INFO) {
            throw WrongPacketTypeReceived(
                expectedPacketType = awaitedPacketType.value, receivedPacketType = packet.packetType
            )
        } else when (packet.packetType) {
            USER_INFO -> updateOtherUserInfo(packet as UserInfo)
            ACCEPTANCE_BLOCKS -> {

            }

            TRANSACTION_RESULT -> {
                doNextStepOnResult(packet as TransactionResult)
            }

            else -> throw WrongPacketTypeReceived(
                message = "Sending side can not receive ${packet.packetType}"
            )
        }


    }


    suspend fun doNextStepOnResult(result: TransactionResult) {
        when (currentStep.value) {
            OFFER_SENT -> if (result.value is Success) {
//                doStep(SEND_BANKNOTES)
            } else {
                resetTransactionController()
            }

            BANKNOTES_LIST_SENT -> {/*nothing here, assume banknotes sending is successful*/
            }

            WAITING_FOR_ACCEPTANCE_BLOCKS -> {/*nothing here, no result should be expected here, only acceptance blocks*/
            }

            SIGN_AND_SEND_BLOCK -> {
                if (transactionDataBuffer.value.allBanknotesProcessed) {
                    if (result.value is Success) {

                    } else {
                    }

                }
            }

            FINISH -> {}
            else -> throw WrongPacketTypeReceived(message = "Should not receive any results at this step")
        }
    }

//    suspend fun doStep(step: ForSender) {
//        if (step.canFollowStep(currentStep.value)) {
//            when (step) {
//                INITIAL -> {
//                    resetTransaction()
//                    updateStep(INITIAL)
//                }
//
//                INIT_TRANSACTION -> {
//                    updateStep(INIT_TRANSACTION)
//                    initTransaction()
//                }
//
//                SEND_OFFER -> TODO()
////            WAIT_FOR_RESULT -> TODO()
//                SEND_BANKNOTES -> {
//                    val list = transactionDataBuffer.value.banknotesList?.list
//                    list?.let { } ?: {}
//                }
//
//                WAITING_FOR_ACCEPTANCE_BLOCKS -> TODO()
//                SIGN_AND_SEND_BLOCK -> TODO()
////            SAVE_BANKNOTES_TO_WALLET -> TODO()
//                FINISH -> {
//
//                    doStep(INITIAL)
//                }
//            }
//        } else throw InvalidStepsOrderException(lastStep = currentStep.value, attemptedStep = step)
//    }


    //Amount should be already set via UI before init transaction is called
    private suspend fun initTransaction() {
        updateStep(INIT_TRANSACTION)
        _transactionDataBuffer.update { it.copy(isAmountAvailable = null) }
        val amount = transactionDataBuffer.value.amountRequest?.amount
            ?: throw Exception("AmountRequest in buffer is still null, can't initialize transaction")
        val resultBanknotes = getBanknotesFromAmount(amount)
        yield()
        if (resultBanknotes == null) {
            _transactionDataBuffer.update { it.copy(isAmountAvailable = false) }
        } else {
            _transactionDataBuffer.update {
                it.copy(
                    isAmountAvailable = true, banknotesList = BanknotesList(resultBanknotes)
                )
            }
        }
    }

    private suspend fun sendOffer() {
        if (transactionDataBuffer.value.isAmountAvailable == true) {
            updateStep(OFFER_SENT)
            outputDataPacketChannel.send(transactionDataBuffer.value.amountRequest!!)
        } else throw Exception("Cannot send the offer, the amount is not available")
    }

    private suspend fun sendBanknotesList(banknotesList: BanknotesList) {
        outputDataPacketChannel.send(banknotesList)
    }


    private suspend fun signAndSendBlock() {


    }


    private suspend fun deleteLocalBanknotes() {
        val bnidList =
            transactionDataBuffer.value.banknotesList?.list?.map { it.banknoteWithProtectedBlock.banknote.bnid }
        if (bnidList == null) throw Exception("BanknotesList in buffer is null") else if (bnidList.isEmpty()) throw Exception(
            "BanknnotesList in buffer is empty"
        ) else walletRepository.deleteBanknotesWithBlockchainByBnids(bnidList)
    }

    private fun updateStep(step: ForSender) {
        _currentStep.update { step }
    }

    fun processPacketOnCurrentStep(packet: DataPacketVariant) {
        if (packet.packetType == USER_INFO) {
            updateOtherUserInfo(packet as UserInfo)
        } else when (currentStep.value) {
            INITIAL -> TODO()
            INIT_TRANSACTION -> TODO()
            OFFER_SENT -> TODO()
            BANKNOTES_LIST_SENT -> TODO()
            WAITING_FOR_ACCEPTANCE_BLOCKS -> TODO()
            SIGN_AND_SEND_BLOCK -> TODO()
            FINISH -> TODO()
        }
    }
//    /** This function is designed for a p2p sender.
//     *  Used to check if the new step can be invoked right after some other step.
//     *  Think of it as of a finite-state machine connection validity checker.
//     * @receiver new step to check validity for
//     * @param stepToFollow typically the current transaction step */
//    private fun ForSender.canFollowStep(
//        stepToFollow: ForSender
//    ): Boolean = when (this) {
//        INITIAL -> true
//        INIT_TRANSACTION -> stepToFollow == INITIAL
//        SEND_OFFER -> stepToFollow == INIT_TRANSACTION
////        SEND_BANKNOTES -> stepToFollow == WAIT_FOR_RESULT
//        WAITING_FOR_ACCEPTANCE_BLOCKS -> stepToFollow == SEND_BANKNOTES || stepToFollow == SEND_SIGNED_BLOCK
//// todo later add "|| otherStep == WAIT_FOR_ANY_RESULT" for waiting for result after verification from the receiving side
//        SIGN_AND_SEND_BLOCK -> stepToFollow == WAITING_FOR_ACCEPTANCE_BLOCKS
//        SEND_SIGNED_BLOCK -> stepToFollow == SIGN_AND_SEND_BLOCK
//        /** Wait for the other side to send result of their last action */
//        //right now is only for waiting for the result of the whole transaction
//        //todo later change logic to wait for result after sending every signed block not only after sending all blocks
////        WAIT_FOR_RESULT -> stepToFollow == SEND_SIGNED_BLOCK
////        SAVE_BANKNOTES_TO_WALLET -> stepToFollow == WAIT_FOR_RESULT
//        FINISH -> stepToFollow == SEND_SIGNED_BLOCK
//        //not needed right now for sending side, no results are sent, only data
////        SEND_RESULT ->
//        SEND_BANKNOTES -> TODO()
//    }


    /**
     *  Subset Sum Problem, NP-Hard
     *  @see <a href="https://en.wikipedia.org/wiki/Subset_sum_problem">Subset Sum Problem - Wikipedia</a>
     * */
    private suspend fun getBanknotesFromAmount(amount: Int): List<BanknoteWithBlockchain>? {
        val resultAmounts =
            findBanknotesWithSum(walletRepository.getBanknotesIdsAndAmounts(), targetSum = amount)
                ?: return null
        val resultBnids = resultAmounts.map { it.bnid }
        return walletRepository.getBanknotesWithBlockchainByBnids(resultBnids)
    }
}