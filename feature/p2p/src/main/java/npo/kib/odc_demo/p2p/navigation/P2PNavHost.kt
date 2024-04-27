package npo.kib.odc_demo.p2p.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import npo.kib.odc_demo.atm.navigation.atmScreen
import npo.kib.odc_demo.atm.navigation.navigateToATMScreen
import npo.kib.odc_demo.p2p.receive_screen.navigation.navigateToReceiveScreen
import npo.kib.odc_demo.p2p.receive_screen.navigation.receiveScreen
import npo.kib.odc_demo.p2p.rememberP2PCommonState
import npo.kib.odc_demo.p2p.send_screen.navigation.navigateToSendScreen
import npo.kib.odc_demo.p2p.send_screen.navigation.sendScreen

@Composable
fun P2PNavHost(
    modifier: Modifier = Modifier,
    onHistoryClick: () -> Unit,
) {
    val p2pCommonState = rememberP2PCommonState()
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