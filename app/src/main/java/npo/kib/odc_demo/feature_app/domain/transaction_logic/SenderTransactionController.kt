package npo.kib.odc_demo.feature_app.domain.transaction_logic

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnection
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForSender
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForSender.*
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.TransactionRole.Sender


abstract class SenderTransactionController(
    val p2pConnection: P2PConnection, val walletRepository: WalletRepository
) : TransactionController(role = Sender) {

    private val _currentStep: MutableStateFlow<ForSender> = MutableStateFlow(INITIAL)
    override val currentStep: StateFlow<ForSender> = _currentStep.asStateFlow()


    @Throws(InvalidStepsOrderException::class)
    suspend fun doStep(step: ForSender) {
        if (step.canFollowStep(currentStep.value)) {
            when (step) {
                INITIAL -> TODO()
                INIT_TRANSACTION -> TODO()
                SEND_OFFER -> TODO()
                WAIT_FOR_RESULT -> TODO()
                SEND_BANKNOTES -> TODO()
                WAITING_FOR_ACCEPTANCE_BLOCKS -> TODO()
                SIGN_BLOCK -> TODO()
                SEND_SIGNED_BLOCK -> TODO()
                SAVE_BANKNOTES_TO_WALLET -> TODO()
            }
        }
        else throw InvalidStepsOrderException(
            lastStep = currentStep.value, attemptedStep = step
        )

    }







    /** This function is designed for a p2p sender.
     *  Used to check if the new step can be invoked right after some other step.
     *  Think of it as of a finite-state machine connection validity checker.
     * @receiver new step to check validity for
     * @param stepToFollow typically the current transaction step */
    private fun ForSender.canFollowStep(
        stepToFollow: ForSender
    ): Boolean = when (this) {
        INITIAL -> true
        INIT_TRANSACTION -> stepToFollow == INITIAL
        SEND_OFFER -> stepToFollow == INIT_TRANSACTION
        SEND_BANKNOTES -> stepToFollow == WAIT_FOR_RESULT
        WAITING_FOR_ACCEPTANCE_BLOCKS -> stepToFollow == SEND_BANKNOTES || stepToFollow == SEND_SIGNED_BLOCK //todo later add "|| otherStep == WAIT_FOR_ANY_RESULT" for waiting for result after verification from the receiving side
        SIGN_BLOCK -> stepToFollow == WAITING_FOR_ACCEPTANCE_BLOCKS
        SEND_SIGNED_BLOCK -> stepToFollow == SIGN_BLOCK
        /** Wait for the other side to send result of their last action */
        //right now is only for waiting for the result of the whole transaction
        //todo later change logic to wait for result after sending every signed block not only after sending all blocks
        WAIT_FOR_RESULT -> stepToFollow == SEND_SIGNED_BLOCK
        SAVE_BANKNOTES_TO_WALLET -> stepToFollow == WAIT_FOR_RESULT
        //not needed right now for sending side, no results are sent, only data
//        SEND_RESULT ->
    }

}