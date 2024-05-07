package npo.kib.odc_demo.core.database.model

import androidx.room.ColumnInfo
import androidx.room.TypeConverters
import npo.kib.odc_demo.core.database.BlockchainConverter
import npo.kib.odc_demo.core.wallet.model.ProtectedBlock
import java.security.PublicKey
import java.util.UUID

/**
 * Accompanying block for additional verification on server
 * Inseparable part of the banknote, containing information about
 * the current owner. Has to be recreated when the banknote changes hands.
 * @param refUuid Link to a [BlockEntity]
 */
@TypeConverters(BlockchainConverter::class)
data class ProtectedBlockEntity(
    val parentSok: PublicKey?,
    val parentSokSignature: String?,
    val parentOtokSignature: String?,
    val refUuid: UUID?,
    val sok: PublicKey?,
    val sokSignature: String?,
    val otokSignature: String,
    val transactionSignature: String,
    @ColumnInfo(name = "protected_time")
    val time: Int
)


fun ProtectedBlock.asDatabaseEntity() = ProtectedBlockEntity(
    parentSok = parentSok,
    parentSokSignature = parentSokSignature,
    parentOtokSignature = parentOtokSignature,
    refUuid = refUuid,
    sok = sok,
    sokSignature = sokSignature,
    otokSignature = otokSignature,
    transactionSignature = transactionSignature,
    time = time)


fun ProtectedBlockEntity.asDomainModel() = ProtectedBlock(
    parentSok = parentSok,
    parentSokSignature = parentSokSignature,
    parentOtokSignature = parentOtokSignature,
    refUuid = refUuid,
    sok = sok,
    sokSignature = sokSignature,
    otokSignature = otokSignature,
    transactionSignature = transactionSignature,
    time = time
)
