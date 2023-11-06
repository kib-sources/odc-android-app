package npo.kib.odc_demo.feature_app.presentation.p2p_screens.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.P2PCommonState
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.atm_screen.navigation.atmScreen
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.atm_screen.navigation.navigateToATMScreen
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.navigation.navigateToReceiveScreen
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.navigation.receiveScreen
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen.navigation.navigateToSendScreen
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen.navigation.sendScreen

@Composable
fun P2PNavHost(modifier: Modifier = Modifier,
               startingP2PRoute: String = p2pRootRoute,
               onHistoryClick: () -> Unit,
               p2pCommonState: P2PCommonState,
               startDestination: String = p2pSelectionGraphRoutePattern
) {
    val navController = p2pCommonState.navController
    NavHost(
        navController = navController, startDestination = startDestination, modifier = modifier
    ) {
        p2pSelectionGraph(startingP2PRoute = startingP2PRoute,
                          onHistoryClick = onHistoryClick,
                          onATMButtonClick = navController::navigateToATMScreen,
                          onReceiveButtonClick = navController::navigateToReceiveScreen,
                          onSendButtonClick = navController::navigateToSendScreen,
                          nestedGraphs = {
                              atmScreen()
                              receiveScreen()
                              sendScreen()
                          })
    }
}