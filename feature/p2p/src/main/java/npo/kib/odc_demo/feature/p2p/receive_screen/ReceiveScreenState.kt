package npo.kib.odc_demo.feature.p2p.receive_screen

import npo.kib.odc_demo.core.connectivity.bluetooth.BluetoothState
import npo.kib.odc_demo.feature.p2p.receive_screen.ReceiveUiState.Initial
import npo.kib.odc_demo.core.transaction_logic.model.TransactionDataBuffer
import npo.kib.odc_demo.core.transaction_logic.model.TransactionStatus.ReceiverTransactionStatus

internal data class ReceiveScreenState(
    val uiState: ReceiveUiState = Initial,
    val transactionDataBuffer: TransactionDataBuffer = TransactionDataBuffer(),
    val bluetoothState: BluetoothState = BluetoothState()
)

internal sealed interface ReceiveUiState {
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