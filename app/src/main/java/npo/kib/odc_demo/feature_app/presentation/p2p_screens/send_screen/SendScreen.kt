package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SendRoute(
    viewModel: SendViewModelNew
) {
    val sendUiState by viewModel.uiState.collectAsStateWithLifecycle()

    SendScreen(uiState = sendUiState, onEvent = viewModel::onEvent)

}

@Composable
private fun SendScreen(uiState: SendUiState, onEvent: (SendScreenEvent) -> Unit) {

    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "SEND_SCREEN! ", modifier = Modifier.align(Alignment.CenterHorizontally))
        when (uiState){
            SendUiState.Initial -> SendScreenSubScreens.InitialScreen()
            SendUiState.Connected -> TODO()
            SendUiState.Connecting -> TODO()
            SendUiState.ConnectionAccepted -> TODO()
            SendUiState.ConnectionRejected -> TODO()
            SendUiState.Finish -> TODO()
            SendUiState.Interrupted -> TODO()
            SendUiState.Retry -> TODO()
            SendUiState.Searching -> TODO()
            SendUiState.SendAccepted -> TODO()
            SendUiState.SendRejected -> TODO()
            SendUiState.Sending -> TODO()
            is SendUiState.ShowingUsersList -> TODO()
            SendUiState.Success -> TODO()
        }
    }

}

internal object SendScreenSubScreens {
    internal val p2pSendSearchingRoute = "send_searching"

    @Composable
    fun InitialScreen() {
        Text(text = "Initial Screen")
    }

    @Composable
    private fun SearchingScreen(onNavigateToUsersList: () -> Unit) {

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