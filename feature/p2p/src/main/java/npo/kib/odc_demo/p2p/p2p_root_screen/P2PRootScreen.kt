package npo.kib.odc_demo.p2p.p2p_root_screen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import npo.kib.odc_demo.core.design_system.components.P2PSelectionButtonRow
import npo.kib.odc_demo.core.design_system.ui.ThemePreviews
import npo.kib.odc_demo.ui.components.HistoryBlock

@Composable
fun P2PRootScreen(
    onHistoryClick: () -> Unit,
    onATMButtonClick: () -> Unit,
    onReceiveButtonClick: () -> Unit,
    onSendButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        P2PSelectionButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            /*.padding(20.dp)*/,
            onclickATM = onATMButtonClick,
            onclickReceive = onReceiveButtonClick,
            onClickSend = onSendButtonClick
        )
        HistoryBlock(
            onHistoryClick = onHistoryClick,
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth()
            /*.padding(20.dp)*/
        )
    }
}

@ThemePreviews
@Composable
private fun P2PRootScreenPreview() {
    P2PRootScreen({}, {}, {}, {})
}