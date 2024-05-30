package npo.kib.odc_demo.core.connectivity.nfc.types

object ApduCommands {
    const val CONNECTED: Byte = 0x1
    const val REJECTED: Byte = 0x2
    const val REQUEST: Byte = 0x3
    const val RECEIVED: Byte = 0x4
    const val FROM_ATM: Byte = 0x5
    const val FROM_CLIENT: Byte = 0x6
    const val END_OF_MESSAGE: Byte = 0x7
    const val WAIT: Byte = 0x8
    const val ERROR: Byte = 0x9
}