package npo.kib.odc_demo.core.connectivity.bluetooth

sealed interface BluetoothConnectionResult {
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