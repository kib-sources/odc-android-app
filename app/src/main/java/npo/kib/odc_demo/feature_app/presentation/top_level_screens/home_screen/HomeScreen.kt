package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.BalanceBlock
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.navigation.P2PNavHost
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.navigation.p2pRootRoute
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.rememberP2PCommonState
import npo.kib.odc_demo.ui.theme.ODCAppTheme


@Composable
fun HomeRoute(){

}

@Composable
fun HomeScreen(modifier: Modifier = Modifier,
               viewModelNew: HomeViewModelNew = hiltViewModel(),
               /*ODCAppState : ODCAppState,*/
               onHistoryClick: () -> Unit
) {
//    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
//        val (buttonRow,balanceBlock) = createRefs()
//        val buttonRowHorGuideline = createGuidelineFromTop(0.35f)
//        val verticalCenterGuideline = createGuidelineFromStart(0.5f)
    val p2pCommonState = rememberP2PCommonState()

    /*LaunchedEffect(key1 = true){
        p2pCommonState.popToRoot()
    }*/
    Column {
        //todo pass AppUser data to BalanceBlock
        Spacer(modifier = Modifier.weight(0.1f))
        BalanceBlock(balance = 10000,
            modifier = Modifier
                .align(CenterHorizontally)
                .weight(1f))
        P2PNavHost(
            modifier = Modifier
                    .align(CenterHorizontally)
                    .weight(4f),
            startingP2PRoute = p2pRootRoute,
            onHistoryClick = onHistoryClick,
            p2pCommonState = p2pCommonState/*rememberP2PCommonState()*/
        )




    }
}

@Preview(showSystemUi = false)
@Composable
private fun HomePreview() {
    ODCAppTheme {
        HomeScreen(onHistoryClick = {})
    }
}