package npo.kib.odc_demo.feature_app.domain.model

import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.DataPacketType

/**
 * Represents an identifiable [ByteArray]
 * */
data class DataPacket (
    val packetType : DataPacketType,
    val bytes : ByteArray
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
