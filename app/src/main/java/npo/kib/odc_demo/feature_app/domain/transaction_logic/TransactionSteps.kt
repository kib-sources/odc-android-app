package npo.kib.odc_demo.feature_app.domain.transaction_logic

import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.TransactionRole.*

//todo right now not needed, later can change awaited packet type with changing steps and come up with something else
sealed interface TransactionSteps {
    val role: TransactionRole

    enum class ForReceiver : TransactionSteps {
        INITIAL,
        WAIT_FOR_OFFER,
        REJECT_OFFER,
        ACCEPT_OFFER,
        WAIT_FOR_BANKNOTES,
        INIT_VERIFICATION, //steps 2-4
        SEND_ACCEPTANCE_BLOCKS, //or SEND_UNSIGNED_BLOCK
        VERIFY_SIGNATURE,
        SAVE_BANKNOTES_TO_WALLET,
        SEND_RESULT;

        override val role = Receiver
    }

    enum class ForSender : TransactionSteps {
        INITIAL,
        INIT_TRANSACTION,
        SEND_OFFER,
//        WAIT_FOR_RESULT,
        SEND_BANKNOTES,
        WAITING_FOR_ACCEPTANCE_BLOCKS, //OR WAITING_FOR_UNSIGNED_BLOCK
        SIGN_AND_SEND_BLOCK, //step 5
        FINISH
//        , SAVE_BANKNOTES_TO_WALLET
        ;
        //not needed right now for sending side, sender does not need to send any results, only data
//       ,SEND_RESULT;

        override val role = Sender
    }

    sealed interface TransactionRole {
        data object Receiver : TransactionRole
        data object Sender : TransactionRole
    }
}