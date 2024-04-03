package npo.kib.odc_demo.feature_app.presentation.top_level_screens.wallet_details_screen


import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import npo.kib.odc_demo.feature_app.presentation.common.components.ODCGradientButton
import npo.kib.odc_demo.feature_app.presentation.common.components.ODCPlainButton
import npo.kib.odc_demo.ui.theme.ODCAppTheme

@Composable
fun WalletDetailsRoute(
    onBackClick: () -> Unit, viewModel: WalletDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    WalletDetailsScreen(uiState, viewModel::updateInfo, onBackClick)
}


@Composable
private fun WalletDetailsScreen(
    uiState: WalletDetailsState, updateInfo: () -> Unit, onBackClick: () -> Unit
) {
    //show all banknotes in list :
    // 1x 1000
    // 2x 100
    // 300x 1
    // etc ...
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalArrangement = spacedBy(5.dp),
    ) {
        Text(
            text = "User name:\n${uiState.userInfo.userName}",
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Wallet ID:\n${uiState.userInfo.walletId}",
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(text = "Banknotes in wallet:", color = MaterialTheme.colorScheme.onBackground)
        LazyColumn(
            modifier = Modifier.animateContentSize(),
            verticalArrangement = spacedBy(4.dp),
            contentPadding = PaddingValues(
                horizontal = 10.dp, vertical = 4.dp
            )
        ) {
            uiState.banknotesNominalToCountMap.forEach { (nominal, count) ->
                item {
                    Row(Modifier.fillMaxWidth()) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "Nominal: $nominal RUB",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "Count: $count ",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

        }
        Row(horizontalArrangement = spacedBy(10.dp)) {
            ODCGradientButton(
                Modifier.weight(1f),
                text = "Update information",
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                onClick = updateInfo
            )
            ODCPlainButton(
                Modifier.weight(1f),
                text = "Go back",
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                color = Color.White,
                onClick = onBackClick
            )
        }
    }
}


@Preview
@Composable
private fun Preview() {
    ODCAppTheme {
        WalletDetailsScreen(uiState = WalletDetailsState(), {}, {})
    }
}