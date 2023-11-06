package npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen

sealed interface ReceiveScreenEvent {
    data class SetAdvertising(val active: Boolean) : ReceiveScreenEvent
    data class ReactToOffer(val accept: Boolean) : ReceiveScreenEvent
    data object Reset : ReceiveScreenEvent

}
