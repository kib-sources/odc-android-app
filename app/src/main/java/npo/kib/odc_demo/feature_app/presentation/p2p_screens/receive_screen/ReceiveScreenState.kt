package npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen

import npo.kib.odc_demo.feature_app.data.p2p.bluetooth.BluetoothState
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionDataBuffer
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionStatus.ReceiverTransactionStatus

data class ReceiveScreenState(
    val uiState: ReceiveUiState = ReceiveUiState.Initial,
    val transactionDataBuffer: TransactionDataBuffer = TransactionDataBuffer(),
    val bluetoothState: BluetoothState = BluetoothState()
)

sealed interface ReceiveUiState {
    data object Initial : ReceiveUiState
    data object Loading : ReceiveUiState
    data object Advertising : ReceiveUiState
    data class InTransaction(val status: ReceiverTransactionStatus) : ReceiveUiState
    data class OperationResult(val result: ResultType) : ReceiveUiState {
        sealed interface ResultType {
            data object Success : ResultType
            data class Failure(val failureMessage: String) : ResultType
        }
    }
}