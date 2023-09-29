package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen

import androidx.compose.ui.focus.FocusState

sealed class SendScreenEvent{
    //Might need to change to data class to pass some value
    object StartSearching : SendScreenEvent()

    data class ChooseUser(val id: Int) : SendScreenEvent()

    data class ChangeAmountFieldFocus(val focusState: FocusState) : SendScreenEvent()
    data class EnterAmount(val value: Int) : SendScreenEvent()
    object ConfirmAmount : SendScreenEvent()

    object RetryOnInterrupted : SendScreenEvent()
    object Cancel : SendScreenEvent()
    object Finish : SendScreenEvent()
}
