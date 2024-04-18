package npo.kib.odc_demo.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import npo.kib.odc_demo.feature_app.domain.model.user.AppUser
import npo.kib.odc_demo.core.design_system.components.BalanceBlock
import npo.kib.odc_demo.core.design_system.components.ODCBottomBar
import npo.kib.odc_demo.core.design_system.components.ODCTopBar
import npo.kib.odc_demo.feature_app.presentation.common.navigation.TopLevelDestination
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.navigation.P2PNavHost
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.rememberP2PCommonState
import npo.kib.odc_demo.ui.DevicePreviews
import npo.kib.odc_demo.ui.ThemePreviews
import npo.kib.odc_demo.ui.theme.ODCAppTheme


@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    onWalletDetailsClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    val homeState by viewModel.homeScreenState.collectAsStateWithLifecycle()
    HomeScreen(
        homeState = homeState,
        onWalletDetailsClick = onWalletDetailsClick,
        onHistoryClick = onHistoryClick,
        updateBalanceAndUserInfo = viewModel::updateBalanceAndAppUserInfo
    )
}

@Composable
private fun HomeScreen(
    homeState: HomeScreenState,
    onWalletDetailsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    updateBalanceAndUserInfo: () -> Unit
) {
    val p2pCommonState = rememberP2PCommonState()
    Column(
        Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        Spacer(modifier = Modifier.weight(0.1f))
        npo.kib.odc_demo.core.design_system.components.BalanceBlock(
            balance = homeState.balance,
            modifier = Modifier
                .align(CenterHorizontally)
                .weight(1f),
            appUser = homeState.currentUser,
            isUpdatingBalanceAndInfo = homeState.isUpdatingBalanceAndInfo,
            refreshBalanceAndUserInfo = updateBalanceAndUserInfo,
            onWalletDetailsClick = onWalletDetailsClick
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
        HomeScreen(onHistoryClick = {}, homeState = HomeScreenState(
            balance = 0, currentUser = AppUser(
                userName = "", walletId = ""
            ), isUpdatingBalanceAndInfo = false
        ), updateBalanceAndUserInfo = { }, onWalletDetailsClick = {})
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
                npo.kib.odc_demo.core.design_system.components.ODCTopBar(modifier = Modifier/*.height(topBarHeightPercentage)*/)
            }, bottomBar = {
                npo.kib.odc_demo.core.design_system.components.ODCBottomBar(
                    destinations = TopLevelDestination.entries,
                    {},
                    currentDestination = null,
                    modifier = Modifier/*.height(bottomBarHeightPercentage)*/
                )
            }) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues = paddingValues)) {
                    HomeScreen(onHistoryClick = {}, homeState = HomeScreenState(
                        balance = 0, currentUser = AppUser(
                            userName = "", walletId = ""
                        ), isUpdatingBalanceAndInfo = false
                    ), updateBalanceAndUserInfo = { }, onWalletDetailsClick = {})
                }
            }
        }
    }
}