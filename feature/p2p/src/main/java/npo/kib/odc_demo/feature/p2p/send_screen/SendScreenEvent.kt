package npo.kib.odc_demo.feature.p2p.send_screen

import npo.kib.odc_demo.core.model.CustomBluetoothDevice

internal sealed interface SendScreenEvent {
    data class SetDiscovering(val active: Boolean) : SendScreenEvent
    data object Disconnect : SendScreenEvent
    data class ConnectToDevice(val device: CustomBluetoothDevice) : SendScreenEvent
    data class TryConstructAmount(val amount : Int) : SendScreenEvent
    data object CancelConstructingAmount : SendScreenEvent
    data object TrySendOffer : SendScreenEvent

//    todo later add option to cancel current operation before it's completed
//    data object Cancel : SendScreenEvent()
}