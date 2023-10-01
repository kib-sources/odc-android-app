package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SendScreen(sendViewModel : SendViewModelNew = hiltViewModel()){
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(color = Color.Red, modifier = Modifier.fillMaxSize()) {
        }
        Text(text = "SEND_SCREEN! ", modifier = Modifier.align(Alignment.Center))
    }

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