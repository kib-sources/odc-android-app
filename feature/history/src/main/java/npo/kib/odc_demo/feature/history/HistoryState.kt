package npo.kib.odc_demo.feature.history

import npo.kib.odc_demo.core.model.WalletTransaction
import npo.kib.odc_demo.feature.history.util.FilterType
import npo.kib.odc_demo.feature.history.util.SortType

//TODO: later can make transactions database api support pagination (request not all transactions but only a portion)
internal data class HistoryState(
    val uiState: HistoryUiState = HistoryUiState.Loading,
    val transactionList: List<WalletTransaction> = emptyList()
){
    val isEmpty: Boolean get() = transactionList.isEmpty()

}

internal sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data object ShowingHistory : HistoryUiState
}


internal sealed interface HistoryScreenEvent {

    data object UpdateTransactionsList : HistoryScreenEvent

    data class SetSortType(val sortType: SortType) : HistoryScreenEvent

    data class ToggleFilter(val filter: FilterType) : HistoryScreenEvent

}