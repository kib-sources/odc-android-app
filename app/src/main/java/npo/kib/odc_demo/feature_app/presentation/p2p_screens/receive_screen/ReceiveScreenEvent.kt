package npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen


sealed interface ReceiveScreenEvent {
    data class SetAdvertising(val active: Boolean) : ReceiveScreenEvent
    data class ReactToConnection(val accept: Boolean) : ReceiveScreenEvent
    data class ReactToOffer(val accept: Boolean) : ReceiveScreenEvent

//    todo later add option to cancel current operation before it's completed
//      data object Cancel : SendScreenEvent()

    /** same as reset but when operation is successful.
     *
     * todo: or probably can navigate to p2p root screen on this event and pop the
     *  send destination backstack entry from backstack*/
    data object Finish : ReceiveScreenEvent
    data object Reset : ReceiveScreenEvent
}
