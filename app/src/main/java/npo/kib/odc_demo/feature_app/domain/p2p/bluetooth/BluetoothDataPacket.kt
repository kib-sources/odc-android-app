package npo.kib.odc_demo.feature_app.domain.p2p.bluetooth

data class BluetoothDataPacket(
    val bytes : ByteArray,
    val fromUserName : String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BluetoothDataPacket

        if (!bytes.contentEquals(other.bytes)) return false
        return fromUserName == other.fromUserName
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + fromUserName.hashCode()
        return result
    }
}
