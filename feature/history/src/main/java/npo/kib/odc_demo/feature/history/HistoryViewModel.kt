package npo.kib.odc_demo.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import npo.kib.odc_demo.core.domain.transaction_history.DeleteTransactionsUseCases
import npo.kib.odc_demo.core.domain.transaction_history.GetTransactionsUseCases
import npo.kib.odc_demo.core.model.WalletTransaction
import npo.kib.odc_demo.feature.history.HistoryScreenEvent.*
import npo.kib.odc_demo.feature.history.HistoryUiState.Loading
import npo.kib.odc_demo.feature.history.HistoryUiState.ShowingHistory
import npo.kib.odc_demo.feature.history.util.FilterType
import npo.kib.odc_demo.feature.history.util.FilterType.Companion.filterWithType
import npo.kib.odc_demo.feature.history.util.SortType
import npo.kib.odc_demo.feature.history.util.SortType.Companion.sortWithType
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
internal class HistoryViewModel @Inject constructor(
    private val getTransactionsUseCases: GetTransactionsUseCases,
    private val deleteTransactionsUseCases: DeleteTransactionsUseCases
) :
    ViewModel() {

    private val _uiState: MutableStateFlow<HistoryUiState> = MutableStateFlow(Loading)

    private val currentFilters: MutableStateFlow<Set<FilterType>> = MutableStateFlow(emptySet())
    private val currentSortType: MutableStateFlow<SortType> = MutableStateFlow(SortType.ByDate())

    private val cachedAllTransactions: MutableStateFlow<List<WalletTransaction>> =
        MutableStateFlow(emptyList())

    private val displayedTransactions: StateFlow<List<WalletTransaction>> =
        combine(
            cachedAllTransactions,
            currentFilters,
            currentSortType
        ) { transactions, filters, sortType ->
            updateUiState(Loading)
            var filteredList: List<WalletTransaction> = transactions
            filters.forEach {
                coroutineContext.ensureActive()
                filteredList = filteredList.filterWithType(it)
            }
            val result = filteredList.sortWithType(sortType)
            updateUiState(ShowingHistory)
            result
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )


    val historyState: StateFlow<HistoryState> = combine(
        _uiState,
        displayedTransactions
    ) { uiState, transactionList ->
        HistoryState(
            uiState = uiState,
            transactionList = transactionList
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        HistoryState()
    )


    init {
        onEvent(UpdateTransactionsList)
    }

    fun onEvent(event: HistoryScreenEvent) {
        runSuspendLoading {
            when (event) {
                UpdateTransactionsList -> loadAllTransactions()
                is SetSortType -> currentSortType.update { event.sortType }
                is ToggleFilter -> currentFilters.update { filterSet ->
                    val elem = event.filter
                    with(filterSet) {
                        if (contains(elem)) this - elem else this + elem
                    }
                }
            }
        }
    }


    private suspend fun loadAllTransactions() {
        cachedAllTransactions.update { getTransactionsUseCases.getAllTransactions().first() }
    }


    private fun updateUiState(newState: HistoryUiState) = _uiState.update { newState }


    private fun runSuspendLoading(lambda: suspend () -> Unit) {
        viewModelScope.launch {
            updateUiState(Loading)
            lambda()
            updateUiState(ShowingHistory)
        }
    }

}

