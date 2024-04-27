package npo.kib.odc_demo.wallet.model.data_packet.variants

import kotlinx.serialization.Serializable
import npo.kib.odc_demo.wallet.model.data_packet.DataPacketType.SIGNED_BLOCK
import npo.kib.odc_demo.wallet.model.serialization.PublicKeySerializerNotNull
import npo.kib.odc_demo.wallet.model.serialization.UUIDSerializer
import npo.kib.odc_demo.wallet.model.serialization.UUIDSerializerNotNull
import java.security.PublicKey
import java.util.UUID


/**
 * A block of the public blockchain for each banknote
 * @param bnid Banknote id
 * @param otok One-time open key
 * */
@Serializable
data class Block(
    @Serializable(with = UUIDSerializerNotNull::class)
    val uuid: UUID,
    @Serializable(with = UUIDSerializer::class)
    val parentUuid: UUID?,
    val bnid: String,
    @Serializable(with = PublicKeySerializerNotNull::class)
    val otok: PublicKey,
    val time: Int,
    val magic: String?,
    val transactionHash: String?,
    val transactionHashSignature: String?,
) : DataPacketVariant(packetType = SIGNED_BLOCK) {

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