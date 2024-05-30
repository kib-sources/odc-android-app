package npo.kib.odc_demo.core.transaction_logic.model

import npo.kib.odc_demo.core.wallet.model.BanknoteWithBlockchain
import npo.kib.odc_demo.core.wallet.model.data_packet.variants.*

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
    val currentlyProcessedBanknoteIndex: Int = 0,
    val lastAcceptanceBlocks: AcceptanceBlocks? = null,
    val lastSignedBlock: Block? = null, //might be useful later on
    val allBanknotesProcessed: Boolean = false,
    val finalBanknotesToDB: List<BanknoteWithBlockchain> = emptyList(),
    val transactionResult: TransactionResult? = null,
    val lastException: String? = null
)