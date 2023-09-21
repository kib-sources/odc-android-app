package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.p2p_screens.receive_screen

import androidx.compose.ui.focus.FocusState

sealed class ReceiveScreenEvent {
    //Might need to change to data class to pass some value
    object StartSearching : ReceiveScreenEvent()

    data class ChooseUser(val id: Int) : ReceiveScreenEvent()

    data class ChangeAmountFieldFocus(val focusState: FocusState) : ReceiveScreenEvent()
    data class EnterAmount(val value: Int) : ReceiveScreenEvent()
    object ConfirmAmount : ReceiveScreenEvent()

    object RetryOnInterrupted : ReceiveScreenEvent()
    object Cancel : ReceiveScreenEvent()
    object Finish : ReceiveScreenEvent()

}
