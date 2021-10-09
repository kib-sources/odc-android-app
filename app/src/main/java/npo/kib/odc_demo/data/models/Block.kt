/*
   Декларирование одного блока
 */

package npo.kib.odc_demo.data.models

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import kotlinx.serialization.Serializable
import npo.kib.odc_demo.core.Crypto
import npo.kib.odc_demo.core.checkHashes
import npo.kib.odc_demo.core.getStringPem
import npo.kib.odc_demo.data.db.BlockchainConverter
import npo.kib.odc_demo.data.p2p.PublicKeySerializer
import npo.kib.odc_demo.data.p2p.PublicKeySerializerNotNull
import npo.kib.odc_demo.data.p2p.UUIDSerializer
import npo.kib.odc_demo.data.p2p.UUIDSerializerNotNull
import java.lang.Exception
import java.security.PublicKey
import java.util.*

@Serializable
@Entity(
    tableName = "block",
    foreignKeys = [ForeignKey(
        entity = Blockchain::class,
        parentColumns = ["bnidKey"],
        childColumns = ["block_bnid"],
        onDelete = CASCADE
    )],
    indices = [Index(value = ["block_bnid"])]
)
@TypeConverters(BlockchainConverter::class)
data class Block(
    /*
    Блок публичного блокчейна к каждой банкноте
    */
    @PrimaryKey
    @Serializable(with = UUIDSerializerNotNull::class)
    val uuid: UUID,

    @Serializable(with = UUIDSerializer::class)
    val parentUuid: UUID?,

    // BankNote id
    @ColumnInfo(name = "block_bnid")
    val bnid: String,

    // One Time Open key
    @Serializable(with = PublicKeySerializerNotNull::class)
    val otok: PublicKey,

    val time: Int,
    val magic: String?,
    val transactionHashValue: ByteArray?,
    val transactionHashSignature: String?,
) {
    fun makeBlockHashValue(): ByteArray {
        return if (parentUuid == null) {
            Crypto.hash(
                uuid.toString(), otok.getStringPem(), bnid, time.toString()
            )
        } else {
            Crypto.hash(
                uuid.toString(), parentUuid.toString(), otok.getStringPem(), bnid, time.toString()
            )
        }
    }

    fun verification(publicKey: PublicKey): Boolean {
        // publicKey -- otok or bok
        if (magic == null) {
            throw Exception("Блок не до конца определён. Не задан magic")
        }
        if (transactionHashValue == null) {
            throw Exception("Блок не до конца определён. Не задан hashValue")
        }
        if (transactionHashSignature == null) {
            throw Exception("Блок не до конца определён. Не задан signature")
        }

        val hashValueCheck = makeBlockHashValue()
        if (!checkHashes(hashValueCheck, transactionHashValue)) {
            throw Exception("Некорректно подсчитан hashValue")
        }
        return Crypto.verifySignature(transactionHashValue, transactionHashSignature, publicKey)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Block

        if (uuid != other.uuid) return false
        if (parentUuid != other.parentUuid) return false
        if (bnid != other.bnid) return false
        if (otok != other.otok) return false
        if (time != other.time) return false
        if (magic != other.magic) return false
        if (transactionHashValue != null) {
            if (other.transactionHashValue == null) return false
            if (!transactionHashValue.contentEquals(other.transactionHashValue)) return false
        } else if (other.transactionHashValue != null) return false
        if (transactionHashSignature != other.transactionHashSignature) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + (parentUuid?.hashCode() ?: 0)
        result = 31 * result + bnid.hashCode()
        result = 31 * result + otok.hashCode()
        result = 31 * result + time
        result = 31 * result + (magic?.hashCode() ?: 0)
        result = 31 * result + (transactionHashValue?.contentHashCode() ?: 0)
        result = 31 * result + (transactionHashSignature?.hashCode() ?: 0)
        return result
    }
}

@Serializable
@TypeConverters(BlockchainConverter::class)
data class ProtectedBlock(
    /*
    Сопроваждающий блок для дополнительного подтверждения на Сервере.
    */
    @Serializable(with = PublicKeySerializer::class)
    val parentSok: PublicKey?,
    val parentSokSignature: String?,
    val parentOtokSignature: String?,

    // Ссылка на Block
    @Serializable(with = UUIDSerializer::class)
    val refUuid: UUID?,

    @Serializable(with = PublicKeySerializer::class)
    val sok: PublicKey?,
    val sokSignature: String?,
    val otokSignature: String,
    val transactionSignature: String,

    @ColumnInfo(name = "protected_time")
    val time: Int
)