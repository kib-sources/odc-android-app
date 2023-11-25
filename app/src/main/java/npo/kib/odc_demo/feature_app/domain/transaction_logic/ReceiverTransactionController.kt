package npo.kib.odc_demo.feature_app.domain.transaction_logic

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnection
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForReceiver
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForReceiver.*
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.TransactionRole.Receiver

abstract class ReceiverTransactionController : TransactionController(role = Receiver) {

    protected abstract val p2pConnection: P2PConnection
    protected abstract val walletRepository: WalletRepository

    private val _currentStep: MutableStateFlow<ForReceiver> = MutableStateFlow(INITIAL)
    override val currentStep: StateFlow<ForReceiver> = _currentStep.asStateFlow()

    private val _transactionDataBuffer: MutableStateFlow<TransactionDataBuffer> =
        MutableStateFlow(TransactionDataBuffer())
    override val transactionDataBuffer: StateFlow<TransactionDataBuffer> = _transactionDataBuffer.asStateFlow()

    private var currentJob: Job? = null

    @Throws(InvalidStepsOrderException::class)
    suspend fun doStep(step: ForReceiver) {
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

    }


    protected abstract fun listenForOffer()

    protected abstract fun sendOfferRejection()

    protected abstract fun sendOfferApproval()


    protected abstract fun listenForBanknotes()

    protected abstract fun initBanknoteVerification()


    protected abstract fun sendAcceptanceBlocks()
//    private suspend fun acceptance(banknoteWithBlockchain: BanknoteWithBlockchain) {
//    }

    protected abstract fun verifyReceivedBlockSignature()


    protected abstract fun saveBanknotesToWallet()


    protected abstract fun sendResult()


    private fun reset() {
        resetJob()
        _currentStep.update { INITIAL }
    }

    private fun resetJob() {
        currentJob?.cancel()
        currentJob = null
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


}

