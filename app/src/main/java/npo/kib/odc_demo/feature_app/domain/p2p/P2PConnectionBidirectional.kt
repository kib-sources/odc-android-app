package npo.kib.odc_demo.feature_app.domain.p2p

// Расширенный интерфейс для p2p соединений с возможностью отправки банкнот
interface P2PConnectionBidirectional : P2PConnection {
    fun startAdvertising()
    fun stopAdvertising()
}