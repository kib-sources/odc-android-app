package npo.kib.odc_demo.feature_app.data.p2p

// Расширенный интерфейс для p2p соеденений с возможностью отправки банкнот
interface P2PConnectionBidirectional : P2PConnection {
    fun startAdvertising()
    fun stopAdvertising()
}