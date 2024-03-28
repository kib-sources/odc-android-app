package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import npo.kib.odc_demo.feature_app.presentation.common.components.BalanceBlock
import npo.kib.odc_demo.feature_app.presentation.common.components.ODCBottomBar
import npo.kib.odc_demo.feature_app.presentation.common.components.ODCTopBar
import npo.kib.odc_demo.feature_app.presentation.common.navigation.TopLevelDestination
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.navigation.P2PNavHost
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.rememberP2PCommonState
import npo.kib.odc_demo.ui.DevicePreviews
import npo.kib.odc_demo.ui.ThemePreviews
import npo.kib.odc_demo.ui.theme.ODCAppTheme


@Composable
fun HomeRoute() {

}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(), onHistoryClick: () -> Unit
) {

    val p2pCommonState = rememberP2PCommonState()

    val homeState by viewModel.homeScreenState.collectAsStateWithLifecycle()


    LaunchedEffect(key1 = true) {
//        p2pCommonState.popToRoot() fixme would pop to root on screen rotation, not only on initial navigation
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(5.dp)
//            .border(2.dp, color = MaterialTheme.colorScheme.onBackground, shape = RoundedCornerShape(10))
    ) {
        Spacer(modifier = Modifier.weight(0.1f))
        BalanceBlock(
            balance = homeState.balance,
            modifier = Modifier
                .align(CenterHorizontally)
                .weight(1f),
            appUser = homeState.currentUser,
            isUpdatingBalanceAndInfo = homeState.isUpdatingBalanceAndInfo,
            refreshBalanceAndUserInfo = viewModel::updateBalanceAndAppUserInfo
        )
        P2PNavHost(
            modifier = Modifier
                .align(CenterHorizontally)
                .weight(4f),
            p2pCommonState = p2pCommonState,
            onHistoryClick = onHistoryClick,
//            refreshBalanceAndUserInfo = viewModel::updateBalanceAndAppUserInfo
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


@ThemePreviews
@DevicePreviews
@Composable
private fun HomeScreenPreview() {
    ODCAppTheme {
        BoxWithConstraints(propagateMinConstraints = false) {
            val topBarHeightPercentage = maxHeight * 0.1f
            val topBarWithBalanceBlockHeightPercentage = maxHeight * 0.25f
            val bottomBarHeightPercentage = maxHeight * 0.07f


            Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                ODCTopBar(modifier = Modifier/*.height(topBarHeightPercentage)*/)
            }, bottomBar = {
                ODCBottomBar(
                    destinations = TopLevelDestination.entries,
                    {},
                    currentDestination = null,
                    modifier = Modifier/*.height(bottomBarHeightPercentage)*/
                )
            }) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues = paddingValues)) {
                    HomeScreen(onHistoryClick = {})
                }
            }
        }
    }
}