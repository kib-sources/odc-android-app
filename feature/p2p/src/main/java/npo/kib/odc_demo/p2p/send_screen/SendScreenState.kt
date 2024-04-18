package npo.kib.odc_demo.p2p.send_screen

import npo.kib.odc_demo.feature_app.data.p2p.bluetooth.BluetoothState
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionDataBuffer
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionStatus.SenderTransactionStatus
import npo.kib.odc_demo.p2p.send_screen.SendUiState.Initial

data class SendScreenState(
    val uiState: SendUiState = Initial,
    val transactionDataBuffer: TransactionDataBuffer = TransactionDataBuffer(),
    val bluetoothState: BluetoothState = BluetoothState()
)

sealed interface SendUiState {
    data object Initial : SendUiState
    data object Loading : SendUiState
    data object Discovering : SendUiState
    data class InTransaction(val status: SenderTransactionStatus) : SendUiState
    data class OperationResult(val result: ResultType) : SendUiState {
        sealed interface ResultType {
            data object Success : ResultType
            data class Failure(val failureMessage: String) : ResultType
        }
    }
}