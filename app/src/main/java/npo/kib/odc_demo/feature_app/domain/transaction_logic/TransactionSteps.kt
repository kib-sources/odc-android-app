package npo.kib.odc_demo.feature_app.domain.transaction_logic

import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.TransactionRole.*

sealed interface TransactionSteps {
    val role : TransactionRole
    enum class ForReceiver : TransactionSteps {
        WAITING_FOR_OFFER,
        REJECT_OFFER,
        WAITING_FOR_BANKNOTES,
        INIT_BLOCK, //steps 2-4
        SEND_UNSIGNED_BLOCK,
        VERIFY_SIGNATURE,
        MAKE_LOCAL_PUSH,
        SEND_RESULT,
        END;
        override val role= Receiver
    }

    enum class ForSender : TransactionSteps {
        INIT,
        SEND_OFFER,
        WAITING_FOR_OFFER_RESULT,
        SEND_BANKNOTES,
        WAITING_FOR_UNSIGNED_BLOCK,
        SIGN_BLOCK, //step 5
        SEND_SIGNED_BLOCK,
        MAKE_LOCAL_PUSH,
        SEND_RESULT,
        END;
        override val role = Sender
    }

    sealed interface TransactionRole {
        data object Receiver : TransactionRole
        data object Sender : TransactionRole
    }
}
