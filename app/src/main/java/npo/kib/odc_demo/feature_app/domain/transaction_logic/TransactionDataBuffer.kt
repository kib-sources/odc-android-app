package npo.kib.odc_demo.feature_app.domain.transaction_logic

import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithBlockchain
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.*

/**
 * Includes all types of [DataPacketVariant] as well as additional information
 * about current transaction state.
 * */
data class TransactionDataBuffer(
    val thisUserInfo: UserInfo? = null,
    val otherUserInfo: UserInfo? = null,
    val amountRequest: AmountRequest? = null,
    val isAmountAvailable: Boolean? = null,
    val banknotesList: BanknotesList? = null,
    val currentlyProcessedBanknoteOrdinal: Int = 0,
    val lastAcceptanceBlocks: AcceptanceBlocks? = null,
    val lastSignedBlock: Block? = null, //might be useful later on
    val allBanknotesProcessed: Boolean = false,
    val finalBanknotesToDB: List<BanknoteWithBlockchain> = emptyList(),
    val transactionResult: TransactionResult? = null,
    val lastException: String? = null
)