package npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen


sealed interface ReceiveScreenEvent {
    data class SetAdvertising(val active: Boolean) : ReceiveScreenEvent
    data object Disconnect : ReceiveScreenEvent
    data class ReactToOffer(val accept: Boolean) : ReceiveScreenEvent

//    todo later add option to cancel current operation before it's completed
//    data object Cancel : SendScreenEvent()

//    todo: probably remove, can navigate to p2p root screen on this event and pop the
//     receiveViewModel from backstack
//       maybe pass a finish callback from p2pRoot through to receiveScreen, sendScreen, atmScreen, etc
//       should save transaction to history? Or just reset everything with p2p, pop the backStack and
//       navigate the inner NavHost to p2pRootScreen.
//       So there will be no "Finish" called from the viewModel but rather from the outside to
//       pop this destination with this viewModel from the backstack altogether.
//    data object Finish : ReceiveScreenEvent
}