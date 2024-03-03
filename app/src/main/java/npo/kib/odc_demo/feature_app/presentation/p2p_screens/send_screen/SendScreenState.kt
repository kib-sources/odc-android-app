package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen

import npo.kib.odc_demo.feature_app.data.p2p.bluetooth.BluetoothState
import npo.kib.odc_demo.feature_app.domain.transaction_logic.SenderTransactionStatus
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionDataBuffer

data class SendScreenState(
    val uiState: SendUiState = SendUiState.Initial,
    val transactionDataBuffer: TransactionDataBuffer = TransactionDataBuffer(),
    val bluetoothState: BluetoothState = BluetoothState()
)

sealed interface SendUiState {
    data object Initial : SendUiState
    data object Discovering : SendUiState
    data object Connecting : SendUiState
    data object Connected : SendUiState
    data object Loading : SendUiState
    data class InTransaction(val status: SenderTransactionStatus) : SendUiState
    data class OperationResult(val result: ResultType) : SendUiState
    sealed interface ResultType {
        data object Success : ResultType
        data class Failure(val failureMessage: String) : ResultType
    }
}