package npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen

import npo.kib.odc_demo.feature_app.domain.model.user.AppUser
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.CustomBluetoothDevice
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionDataBuffer

data class ReceiveScreenState(
    val uiState: ReceiveUiState = ReceiveUiState.Initial,
    val transactionDataBuffer: TransactionDataBuffer = TransactionDataBuffer(),
    val localUser: AppUser? = null,
    val remoteUser: AppUser? = null,
    val isConnected: Boolean = false,
    val connectedDevice: CustomBluetoothDevice? = null
)

sealed interface ReceiveUiState {
    data object Initial : ReceiveUiState
    data object Advertising : ReceiveUiState
    data class ConnectionRequestReceived(val fromDevice: CustomBluetoothDevice) : ReceiveUiState
    data object Connected : ReceiveUiState
    data object OfferReceived : ReceiveUiState
    data object ReceivingAllBanknotes : ReceiveUiState
    data object ProcessingBanknote : ReceiveUiState
    data class Result(val result: ResultType) : ReceiveUiState
    sealed interface ResultType {
        data object Success : ResultType
        data class Failure(val failureMessage: String) : ResultType
    }
}