package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen

import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.CustomBluetoothDevice

sealed interface SendScreenEvent {
    //Might need to change to data class to pass some value
    data object StartSearching : SendScreenEvent
    data class ConnectToUser(val device: CustomBluetoothDevice) : SendScreenEvent

    //    ! these are a part of the particular screen
//    data class ChangeAmountFieldFocus(val focusState: FocusState) : SendScreenEvent()
//    data class EnterAmount(val value: Int) : SendScreenEvent()
    //todo add later "checkIfEnoughBanknotes" event to send before SendOffer.
    // If the amount can be constructed with available banknotes, then send the offer.
    data class SendOffer(val amount: Int) : SendScreenEvent

//    todo later add option to cancel current operation before it's completed
//      data object Cancel : SendScreenEvent()

    /** same as reset but when operation is successful.
     *
     * todo: or probably can navigate to p2p root screen on this event and pop the
     *  send destination backstack entry from backstack*/
    data object Finish : SendScreenEvent
    data object Retry : SendScreenEvent
    /**
     * Reset the viewModel and UI state to initial state
     * */
    data object Reset : SendScreenEvent
}