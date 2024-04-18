package npo.kib.odc_demo.atm

sealed interface ATMScreenEvent{
    data class SendAmountRequestToServer(val amount: Int) : ATMScreenEvent

}