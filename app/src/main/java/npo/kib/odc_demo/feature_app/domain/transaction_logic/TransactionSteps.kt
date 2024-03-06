package npo.kib.odc_demo.feature_app.domain.transaction_logic

sealed interface TransactionSteps {
    val role: TransactionRole

    enum class ForReceiver : TransactionSteps {
        WAITING_FOR_AMOUNT_REQUEST,
        AMOUNT_REQUEST_RECEIVED,
        WAITING_FOR_BANKNOTES_LIST,
        WAITING_FOR_SIGNED_BLOCK,
        WAITING_FOR_RESULT,
        FINISHED;

        override val role = TransactionRole.RECEIVER
    }

    enum class ForSender : TransactionSteps {
        INITIAL,
        WAITING_FOR_OFFER_RESPONSE,
        WAITING_FOR_BANKNOTES_LIST_RECEIVED_RESPONSE,
        WAITING_FOR_ACCEPTANCE_BLOCKS,
        WAITING_FOR_RESULT,
        FINISHED;

        override val role = TransactionRole.SENDER
    }
}

enum class TransactionRole {
    RECEIVER,
    SENDER
}

sealed interface TransactionStatus {
    val role: TransactionRole

    enum class ReceiverTransactionStatus : TransactionStatus {
        WAITING_FOR_OFFER,
        OFFER_RECEIVED,
        RECEIVING_BANKNOTES_LIST,
        BANKNOTES_LIST_RECEIVED,
        CREATING_SENDING_ACCEPTANCE_BLOCKS,
        WAITING_FOR_SIGNED_BLOCK,
        VERIFYING_RECEIVED_BLOCK,
        ALL_BANKNOTES_VERIFIED,
        SAVING_BANKNOTES_TO_WALLET,
        BANKNOTES_SAVED,
        FINISHED_SUCCESSFULLY,
        ERROR;

        override val role: TransactionRole = TransactionRole.RECEIVER
    }

    enum class SenderTransactionStatus : TransactionStatus {
        INITIAL,
        CONSTRUCTING_AMOUNT,
        SHOWING_AMOUNT_AVAILABILITY,
        WAITING_FOR_OFFER_RESPONSE,
        OFFER_ACCEPTED,
        OFFER_REJECTED,
        SENDING_BANKNOTES_LIST,
        WAITING_FOR_BANKNOTES_RECEIVED_RESPONSE,
        WAITING_FOR_ACCEPTANCE_BLOCKS,
        SIGNING_SENDING_NEW_BLOCK,
        ALL_BANKNOTES_PROCESSED,
        DELETING_BANKNOTES_FROM_WALLET,
        BANKNOTES_DELETED,
        FINISHED_SUCCESSFULLY,
        ERROR;

        override val role: TransactionRole = TransactionRole.SENDER
    }
}