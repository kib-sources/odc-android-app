package npo.kib.odc_demo.p2p.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import npo.kib.odc_demo.atm.atm_screen.navigation.atmScreen
import npo.kib.odc_demo.atm.atm_screen.navigation.navigateToATMScreen
import npo.kib.odc_demo.p2p.P2PCommonState
import npo.kib.odc_demo.p2p.receive_screen.navigation.navigateToReceiveScreen
import npo.kib.odc_demo.p2p.receive_screen.navigation.receiveScreen
import npo.kib.odc_demo.p2p_send.navigation.navigateToSendScreen
import npo.kib.odc_demo.p2p_send.navigation.sendScreen

@Composable
fun P2PNavHost(
    modifier: Modifier = Modifier,
    p2pCommonState: P2PCommonState,
    onHistoryClick: () -> Unit,
) {
    val navController = p2pCommonState.navController
    NavHost(
        modifier = modifier,
        startDestination = p2pSelectionGraphRoutePattern,
        navController = navController
    ) {
        p2pSelectionGraph(startingP2PRoute = p2pRootRoute,
            onHistoryClick = onHistoryClick,
            onATMButtonClick = navController::navigateToATMScreen,
            onReceiveButtonClick = navController::navigateToReceiveScreen,
            onSendButtonClick = navController::navigateToSendScreen,
            nestedGraphs = {
                atmScreen(navigateToP2PRoot = p2pCommonState::popToRoot)
                receiveScreen(
                    navigateToP2PRoot = p2pCommonState::popToRoot
                )
                sendScreen(
                    navigateToP2PRoot = p2pCommonState::popToRoot
                )
            })
    }
}