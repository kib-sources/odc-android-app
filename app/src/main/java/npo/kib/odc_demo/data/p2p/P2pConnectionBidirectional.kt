package npo.kib.odc_demo.data.p2p

// Расширенный интерфейс для p2p соеденений с возможностью отправки банкнот
interface P2pConnectionBidirectional : P2pConnection {
    fun startAdvertising()
    fun stopAdvertising()
}