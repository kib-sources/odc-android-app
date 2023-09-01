package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.p2p_screens.request_screen

import androidx.compose.ui.focus.FocusState

sealed class RequestScreenEvent {
    //Might need to change to data class to pass some value
    object StartSearching : RequestScreenEvent()

    data class ChooseUser(val id: Int) : RequestScreenEvent()

    data class ChangeAmountFieldFocus(val focusState: FocusState) : RequestScreenEvent()
    data class EnterAmount(val value: Int) : RequestScreenEvent()
    object ConfirmAmount : RequestScreenEvent()

    object RetryOnInterrupted : RequestScreenEvent()
    object Cancel : RequestScreenEvent()
    object Finish : RequestScreenEvent()

}
