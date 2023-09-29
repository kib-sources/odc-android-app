package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SendScreen(sendViewModel : SendViewModelNew = hiltViewModel()){


}

internal object SendScreenSubScreens {
    internal val p2pSendSearchingRoute = "send_searching"

    @Composable
    private fun SearchingScreen(onNavigateToUsersList : () -> Unit) {

    }

    @Composable
    private fun ShowingUsersListScreen() {

    }

    @Composable
    private fun ConnectingScreen() {

    }

    @Composable
    private fun ConnectedScreen() {

    }

    @Composable
    private fun AcceptedScreen() {

    }

    @Composable
    private fun RejectedScreen() {

    }

    @Composable
    private fun ReceivingScreen() {

    }

    @Composable
    private fun SuccessScreen() {

    }

    @Composable
    private fun InterruptedScreen() {

    }

}