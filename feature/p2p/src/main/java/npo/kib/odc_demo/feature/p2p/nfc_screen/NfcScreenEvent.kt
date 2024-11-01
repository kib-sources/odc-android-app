package npo.kib.odc_demo.feature.p2p.nfc_screen

sealed interface NfcScreenEvent {
    data object StartReceiving : NfcScreenEvent

}