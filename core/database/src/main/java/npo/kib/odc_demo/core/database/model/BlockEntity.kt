@file:OptIn(ExperimentalStdlibApi::class)

package npo.kib.odc_demo.core.database.model

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE
import npo.kib.odc_demo.core.database.BlockchainConverter
import npo.kib.odc_demo.core.wallet.model.data_packet.variants.Block
import java.security.PublicKey
import java.util.UUID


/**
 * A block of the public blockchain for each banknote
 * @param bnid Banknote id
 * @param otok One-time open key
 * @param magic Idk, looks like magic
 * */
@Entity(
    tableName = "block", foreignKeys = [ForeignKey(
        entity = BanknoteWithProtectedBlockEntity::class,
        parentColumns = ["bnid"],
        childColumns = ["block_bnid"],
        onDelete = CASCADE
    )], indices = [Index(value = ["block_bnid"])]
)
@TypeConverters(BlockchainConverter::class)
data class BlockEntity(
    @PrimaryKey
    val uuid: UUID,
    val parentUuid: UUID?,
    @ColumnInfo(name = "block_bnid")
    val bnid: String,
    val otok: PublicKey,
    val time: Int,
    val magic: String?,
    val transactionHash: String?,
    val transactionHashSignature: String?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BlockEntity

        if (uuid != other.uuid) return false
        if (parentUuid != other.parentUuid) return false
        if (bnid != other.bnid) return false
        if (otok != other.otok) return false
        if (time != other.time) return false
        if (magic != other.magic) return false
        if (transactionHash != null) {
            if (other.transactionHash == null) return false
            if (!transactionHash.contentEquals(other.transactionHash)) return false
        } else if (other.transactionHash != null) return false
        return transactionHashSignature == other.transactionHashSignature
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + (parentUuid?.hashCode() ?: 0)
        result = 31 * result + bnid.hashCode()
        result = 31 * result + otok.hashCode()
        result = 31 * result + time
        result = 31 * result + (magic?.hashCode() ?: 0)
        result = 31 * result + (transactionHash?.hexToByteArray()?.contentHashCode() ?: 0)
        result = 31 * result + (transactionHashSignature?.hashCode() ?: 0)
        return result
    }
}

fun Block.asDatabaseEntity() = BlockEntity(
    uuid = uuid,
    parentUuid = parentUuid,
    bnid = bnid,
    otok = otok,
    time = time,
    magic = magic,
    transactionHash = transactionHash,
    transactionHashSignature = transactionHashSignature
)


fun BlockEntity.asDomainModel() = Block(
    uuid = uuid,
    parentUuid = parentUuid,
    bnid = bnid,
    otok = otok,
    time = time,
    magic = magic,
    transactionHash = transactionHash,
    transactionHashSignature = transactionHashSignature
)