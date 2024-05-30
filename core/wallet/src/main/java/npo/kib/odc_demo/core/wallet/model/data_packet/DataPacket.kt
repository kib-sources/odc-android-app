package npo.kib.odc_demo.core.wallet.model.data_packet

import kotlinx.serialization.Serializable

/**
 * Represents an identifiable [ByteArray] sent through a connection
 * */
@Serializable
data class DataPacket(
    val packetType: DataPacketType,
    val bytes: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataPacket

        if (packetType != other.packetType) return false
        return bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int {
        var result = packetType.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }

}
