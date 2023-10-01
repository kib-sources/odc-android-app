package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.BalanceBlock
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.RoundedSquareButtonRow
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.navigation.P2PNavHost
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.rememberP2PCommonState
import npo.kib.odc_demo.ui.theme.ODCAppTheme


@Composable
fun HomeScreen(modifier: Modifier = Modifier,
               viewModelNew: HomeViewModelNew = hiltViewModel(),
               onHistoryClick: () -> Unit
) {
//    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
//        val (buttonRow,balanceBlock) = createRefs()
//        val buttonRowHorGuideline = createGuidelineFromTop(0.35f)
//        val verticalCenterGuideline = createGuidelineFromStart(0.5f)
    //balance block should be common for all main app feature screens
    Column {
//        //todo pass AppUser data to BalanceBlock
        Spacer(modifier = Modifier.weight(0.1f))
        BalanceBlock(modifier = Modifier
                .align(CenterHorizontally)
                .weight(1f))
        P2PNavHost(
            modifier = Modifier.align(CenterHorizontally).weight(4f),
            onHistoryClick = onHistoryClick,
            p2pCommonState = rememberP2PCommonState()
        )


        //Here go either a button row or p2p screens in a separate container, the balance block remains visible this way
        //on the Home screen and child screens but not on any others, like Settings, History or Log-in screens.
        //@Composable P2PContainer
//        RoundedSquareButtonRow(modifier = Modifier.constrainAs(buttonRow) {
//            top.linkTo(buttonRowHorGuideline)
//            centerAround(verticalCenterGuideline)
//        }, onClickButton3)

//    }
    }
}

@Preview(showSystemUi = false)
@Composable
private fun HomePreview() {
    ODCAppTheme {
        HomeScreen(onHistoryClick = {})
    }
}