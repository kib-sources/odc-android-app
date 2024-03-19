package npo.kib.odc_demo.feature_app.presentation.p2p_screens.atm_screen

sealed interface ATMScreenEvent{
    data class SendAmountRequestToServer(val amount: Int) : ATMScreenEvent

}