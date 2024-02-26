package npo.kib.odc_demo.feature_app.domain.transaction_logic

import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.TransactionRole.*

sealed interface TransactionSteps {
    val role: TransactionRole

    enum class ForReceiver : TransactionSteps {
        //        STOPPED,
        WAITING_FOR_AMOUNT_REQUEST,
        AMOUNT_REQUEST_RECEIVED,
        WAITING_FOR_BANKNOTES_LIST,
        WAITING_FOR_SIGNED_BLOCK,
        WAITING_FOR_RESULT,
        FINISHED;
        //todo maybe add FAILED (?)

        override val role = RECEIVER
    }

    enum class ForSender : TransactionSteps {
        INITIAL,
//        TRYING_TO_CONSTRUCT_AMOUNT,
        WAITING_FOR_OFFER_RESPONSE,

        //        WAIT_FOR_RESULT, //todo used to wait for receiver confirmation that they received banknotesList or signature was verified, etc
        WAITING_FOR_BANKNOTES_LIST_RECEIVED_RESPONSE,
        WAITING_FOR_ACCEPTANCE_BLOCKS, //OR WAITING_FOR_UNSIGNED_BLOCK
        WAITING_FOR_RESULT, //step 5
        FINISHED;


        override val role = SENDER
    }

    enum class TransactionRole {
        RECEIVER,
        SENDER
    }
}