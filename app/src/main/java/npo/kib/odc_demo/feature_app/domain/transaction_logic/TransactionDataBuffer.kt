package npo.kib.odc_demo.feature_app.domain.transaction_logic

import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithProtectedBlock
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
    val banknotesList: BanknotesList? = null,
    val currentlyProcessedBanknoteOrdinal: Int? = null,
    val lastSentAcceptanceBlocks: AcceptanceBlocks? = null,
    val lastSentSignedBlock: Block? = null,
    val currentBanknoteToDB: BanknoteWithProtectedBlock? = null,
    val currentBlocksToDB: List<Block>? = null,
    val transactionResult: TransactionResult? = null
)
