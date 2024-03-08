package npo.kib.odc_demo.feature_app.presentation.p2p_screens.p2p_root_screen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import npo.kib.odc_demo.feature_app.presentation.common.components.HistoryBlock
import npo.kib.odc_demo.feature_app.presentation.common.components.RoundedSquareButtonRow
import npo.kib.odc_demo.ui.ThemePreviews

@Composable
fun P2PRootScreen(
    onHistoryClick: () -> Unit,
    onATMButtonClick: () -> Unit,
    onReceiveButtonClick: () -> Unit,
    onSendButtonClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f).padding(20.dp),
            horizontalArrangement = Arrangement.Absolute.Center
        ) {
            RoundedSquareButtonRow(
                modifier = Modifier
                    .align(CenterVertically),
                onclickATM = onATMButtonClick,
                onclickReceive = onReceiveButtonClick,
                onClickSend = onSendButtonClick
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f),
            horizontalArrangement = Arrangement.Absolute.Center
        ) {
            HistoryBlock(
                onHistoryClick = onHistoryClick,
                modifier = Modifier
                    .align(CenterVertically)
                    .padding(20.dp)
            )
        }
    }

}

@ThemePreviews
@Composable
private fun P2PRootScreenPreview() {
    P2PRootScreen(
        {},
        {},
        {},
        {})
}