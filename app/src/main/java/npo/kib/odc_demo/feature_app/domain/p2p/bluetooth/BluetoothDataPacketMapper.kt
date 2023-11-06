package npo.kib.odc_demo.feature_app.domain.p2p.bluetooth


private const val DELIMITER : String = "#"
//PACKET: NAME#BYTEs


fun String.toBluetoothDataPacket(): BluetoothDataPacket {
    val fromUserName = substringBefore("#")
    val data = substringAfter("#")
    return BluetoothDataPacket(
        bytes = data.toByteArray(),
        fromUserName = fromUserName
    )
}

fun ByteArray.toBluetoothDataPacket(): BluetoothDataPacket {
    return this.toString().toBluetoothDataPacket()
}

fun BluetoothDataPacket.toByteArray(): ByteArray {
    return "$fromUserName#$bytes".encodeToByteArray()
}