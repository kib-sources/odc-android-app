package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen

import androidx.compose.ui.focus.FocusState
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.CustomBluetoothDevice

sealed class SendScreenEvent{
    //Might need to change to data class to pass some value
    data object StartSearching : SendScreenEvent()

    data class ChooseUser(val device: CustomBluetoothDevice) : SendScreenEvent()

    data class ChangeAmountFieldFocus(val focusState: FocusState) : SendScreenEvent()
    data class EnterAmount(val value: Int) : SendScreenEvent()
    data object ConfirmAmount : SendScreenEvent()
    data object Retry : SendScreenEvent()
    data object Cancel : SendScreenEvent()
    data object Finish : SendScreenEvent()
}
