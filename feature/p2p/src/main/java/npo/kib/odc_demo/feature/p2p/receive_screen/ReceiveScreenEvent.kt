package npo.kib.odc_demo.feature.p2p.receive_screen


internal sealed interface ReceiveScreenEvent {
    data class SetAdvertising(val active: Boolean) : ReceiveScreenEvent
    data object Disconnect : ReceiveScreenEvent
    data class ReactToOffer(val accept: Boolean) : ReceiveScreenEvent

//    todo later add option to cancel current operation before it's completed
//    data object Cancel : SendScreenEvent()
}