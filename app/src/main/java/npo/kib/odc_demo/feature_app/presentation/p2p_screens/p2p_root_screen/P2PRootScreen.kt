package npo.kib.odc_demo.feature_app.presentation.p2p_screens.p2p_root_screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.BalanceBlock
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.HistoryBlock
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.RoundedSquareButtonRow
import npo.kib.odc_demo.ui.ThemePreviews

@Composable
fun P2PRootScreen(onHistoryClick: () -> Unit,
                  onATMButtonClick: () -> Unit,
                  onReceiveButtonClick: () -> Unit,
                  onSendButtonClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
//        BalanceBlock(modifier = Modifier.align(CenterHorizontally).weight(1f))
        RoundedSquareButtonRow(modifier = Modifier.align(CenterHorizontally).weight(1f),
            onclickATM = onATMButtonClick,
            onclickReceive = onReceiveButtonClick,
            onClickSend = onSendButtonClick
        )
        HistoryBlock(onHistoryClick = onHistoryClick, modifier = Modifier.fillMaxSize()
                .align(CenterHorizontally).padding(20.dp).weight(2f))
    }


}

@ThemePreviews
@Composable
private fun P2PRootScreenPreview() {
    P2PRootScreen({}, {}, {}, {})
}