package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        val isScreenLabelVisible by remember { mutableStateOf(true) }
        AnimatedVisibility(
            visible = isScreenLabelVisible,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Text(text = "Send banknotes", modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        with(SendScreenSubScreens){
        when (uiState) {
            SendUiState.Initial -> InitialScreen()
            SendUiState.Connected -> TODO()
            SendUiState.Connecting -> TODO()
            SendUiState.ConnectionAccepted -> TODO()
            SendUiState.ConnectionRejected -> TODO()
            is SendUiState.OfferSent -> TODO()
            is SendUiState.Result -> TODO()
            SendUiState.Retry -> TODO()
            is SendUiState.Searching -> TODO()
            SendUiState.Sending -> TODO()
        }
        }
    }

}

private object SendScreenSubScreens {
    @Composable
    fun InitialScreen() {
        Text(text = "Initial Screen")
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
        Text(text = "The user accepted the offer")
    }

    @Composable
    private fun RejectedScreen() {
        Text(text = "The user rejected the offer")
    }

    @Composable
    private fun ReceivingScreen() {
        Text(text = "Receiving banknotes")
    }

    @Composable
    private fun SuccessScreen() {
        Text(text = "The user accepted the offer")

    }

    @Composable
    private fun InterruptedScreen() {

    }

}