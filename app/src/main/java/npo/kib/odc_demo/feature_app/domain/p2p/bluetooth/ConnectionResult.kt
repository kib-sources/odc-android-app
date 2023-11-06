package npo.kib.odc_demo.feature_app.domain.p2p.bluetooth

sealed interface ConnectionResult {
    object ConnectionEstablished: ConnectionResult
    data class TransferSucceeded(val packet: BluetoothDataPacket): ConnectionResult
    data class Error(val message: String): ConnectionResult
}