package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen

import npo.kib.odc_demo.feature_app.domain.model.user.AppUser
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.CustomBluetoothDevice
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionDataBuffer
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveUiState

data class SendScreenState(
    val uiState: SendUiState = SendUiState.Initial,
    val transactionDataBuffer: TransactionDataBuffer = TransactionDataBuffer(),
    val localUser: AppUser? = null,
    val remoteUser: AppUser? = null,
    val scannedDevices: List<CustomBluetoothDevice> = emptyList(),
    val pairedDevices: List<CustomBluetoothDevice> = emptyList(),
    val isConnected: Boolean = false,
    val connectedDevice: CustomBluetoothDevice? = null
)

sealed interface SendUiState {
    data object Initial : SendUiState
    data object Searching : SendUiState
    data object Connecting : SendUiState
    data object ConnectionRejected : SendUiState
    data object Connected : SendUiState
    data object OfferSent : SendUiState
    data class OfferResponse(val isAccepted: Boolean) : SendUiState
    data object ProcessingBanknote : SendUiState
    data class Result(val result: ResultType) : SendUiState
    sealed interface ResultType {
        data object Success : ResultType
        data class Failure(val failureMessage: String) : ResultType
    }
}