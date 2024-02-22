package npo.kib.odc_demo.feature_app.domain.model.connection_status

sealed interface BluetoothConnectionResult {

    //    data class ConnectionEstablished(val withDevice: CustomBluetoothDevice?) : BluetoothConnectionResult
    data object ConnectionEstablished : BluetoothConnectionResult
    data class TransferSucceeded(val bytes: ByteArray) : BluetoothConnectionResult {
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

//    data class Error(val message: String) : BluetoothConnectionResult
}