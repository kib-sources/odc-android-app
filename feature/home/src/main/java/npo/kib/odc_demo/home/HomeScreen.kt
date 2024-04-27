package npo.kib.odc_demo.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import npo.kib.odc_demo.core.design_system.ui.theme.ODCAppTheme
import npo.kib.odc_demo.model.user.AppUser
import npo.kib.odc_demo.p2p.navigation.P2PNavHost
import npo.kib.odc_demo.ui.components.BalanceBlock


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

    Column(
        Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        Spacer(modifier = Modifier.weight(0.1f))
        BalanceBlock(
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
            onHistoryClick = onHistoryClick
        )
    }
}

@Preview(showSystemUi = false)
@Composable
fun HomeScreenDefaultPreview() {
    ODCAppTheme {
        HomeScreen(onHistoryClick = {}, homeState = HomeScreenState(
            balance = 0, currentUser = AppUser(
                userName = "", walletId = ""
            ), isUpdatingBalanceAndInfo = false
        ), updateBalanceAndUserInfo = { }, onWalletDetailsClick = {})
    }
}

