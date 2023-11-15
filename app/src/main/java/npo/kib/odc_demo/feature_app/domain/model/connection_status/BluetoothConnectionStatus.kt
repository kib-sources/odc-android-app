package npo.kib.odc_demo.feature_app.domain.model.connection_status

import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.CustomBluetoothDevice

sealed interface BluetoothConnectionStatus {
    data object NoConnection : BluetoothConnectionStatus
    data object Discovering : BluetoothConnectionStatus
    data object WaitingForConnection : BluetoothConnectionStatus
    data object ConnectionInitiated : BluetoothConnectionStatus
    data class ConnectionEstablished(val withDevice: CustomBluetoothDevice?) : BluetoothConnectionStatus
    data class TransferSucceeded(val bytes: ByteArray) : BluetoothConnectionStatus {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TransferSucceeded

            return bytes.contentEquals(other.bytes)
        }

        override fun hashCode(): Int {
            return bytes.contentHashCode()
        }
    }

    data class Error(val message: String) : BluetoothConnectionStatus
    data object Disconnected : BluetoothConnectionStatus
}