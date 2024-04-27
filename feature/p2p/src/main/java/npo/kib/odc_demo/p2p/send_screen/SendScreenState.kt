package npo.kib.odc_demo.p2p.send_screen

import npo.kib.odc_demo.connectivity.bluetooth.BluetoothState
import npo.kib.odc_demo.transaction_logic.model.TransactionDataBuffer
import npo.kib.odc_demo.p2p.send_screen.SendUiState.Initial
import npo.kib.odc_demo.transaction_logic.model.TransactionStatus.SenderTransactionStatus

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