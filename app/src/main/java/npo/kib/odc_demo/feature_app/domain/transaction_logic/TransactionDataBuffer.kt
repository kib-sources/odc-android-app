package npo.kib.odc_demo.feature_app.domain.transaction_logic

import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithBlockchain
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.AcceptanceBlocks
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.AmountRequest
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.BanknotesList
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.Block
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.DataPacketVariant
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.TransactionResult
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.UserInfo

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
    val lastAcceptanceBlocks: AcceptanceBlocks? = null, //might be useful later on
    val lastSignedBlock: Block? = null, //might be useful later on
    val allBanknotesProcessed: Boolean = false,
    val finalBanknotesToDB: List<BanknoteWithBlockchain> = emptyList(),
    val transactionResult: TransactionResult? = null
)
//todo can add lastError property to catch the latest error and show it in UI on ERROR status