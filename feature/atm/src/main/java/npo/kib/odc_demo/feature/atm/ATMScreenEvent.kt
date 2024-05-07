package npo.kib.odc_demo.feature.atm

internal sealed interface ATMScreenEvent{
    data class SendAmountRequestToServer(val amount: Int) : ATMScreenEvent

}