package npo.kib.odc_demo.feature.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import npo.kib.odc_demo.core.design_system.ui.theme.ODCAppTheme
import npo.kib.odc_demo.core.model.WalletTransaction
import npo.kib.odc_demo.feature.history.components.TransactionItem

@Composable
internal fun HistoryRoute(
    onBackClick: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val screenState: HistoryState by viewModel.historyState.collectAsStateWithLifecycle()
    HistoryScreen(
        screenState = screenState,
        onBackClick = onBackClick
    )
}

@Composable
private fun HistoryScreen(
    screenState: HistoryState,
    onBackClick: () -> Unit
) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Transaction history ",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .padding(vertical = 10.dp)
        )

        //todo add filter & sort section toggle button
        var sortFilterSectionVisibleState = remember {
            MutableTransitionState(false)
        }
        AnimatedVisibility(visibleState = sortFilterSectionVisibleState) {

        }
        //animate outer column height to move LazyColumn below when the sort-filter section expands/collapses
        
        val listState = rememberLazyListState()

        LaunchedEffect(key1 = Unit) {
            delay(100)
            listState.animateScrollToItem(0)
        }

        LazyColumn(state = listState) {
            items(
                items = screenState.transactionList,
                key = {
                    it.id!!
                },
                contentType = { },
            ) { item ->
                val dateTime = item.date.toLocalDateTime(TimeZone.UTC)
                TransactionItem(
                    modifier = Modifier.fillMaxWidth(),
                    isWithAtm = item.isWithAtm,
                    isReceived = item.isReceived,
                    amount = item.amount,
                    date = dateTime.date,
                    time = dateTime.time,
                    name = item.otherName,
                    wid = item.otherWid
                )
            }
            item(key = "END_DIVIDER") {
                Column(Modifier.fillMaxWidth()) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .fillMaxWidth(0.4f)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun HSPrev() {
    ODCAppTheme {
        HistoryScreen(
            screenState = HistoryState(
                transactionList = listOf(
                    WalletTransaction(
                        id = 1,
                        otherName = "Alice",
                        otherWid = "asdojsdfqwjfshnadfsaf",
                        isReceived = false,
                        isWithAtm = false,
                        amount = 1234,
                        date = Instant.fromEpochSeconds(10000000)
                    ),
                    WalletTransaction(
                        id = 2,
                        otherName = "Name",
                        otherWid = null,
                        isReceived = true,
                        isWithAtm = true,
                        amount = 5678,
                        date = Instant.fromEpochSeconds(100000000)
                    )
                )
            )
        ) {


        }
    }
}