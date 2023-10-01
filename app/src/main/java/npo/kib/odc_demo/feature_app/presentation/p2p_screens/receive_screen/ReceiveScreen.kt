package npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen

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
fun ReceiveRoute(
    modifier: Modifier = Modifier,
    viewModel: ReceiveViewModelNew = hiltViewModel()
){
    val receiveScreenUiState: ReceiveUiState = viewModel.receiveUiState

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(color = Color.Green, modifier = Modifier.fillMaxSize()) {
        }
        Text(text = "RECEIVE_SCREEN! ", modifier = Modifier.align(Alignment.Center))
    }
//    ReceiveScreen(receiveScreenUiState = )
}



//These will be in a separate module eventuall
@Composable
internal fun ReceiveScreen(
//    receiveScreenState: ReceiveScreenState,
    receiveScreenUiState : ReceiveUiState,
    viewModel: ReceiveViewModelNew
) {

    when (receiveScreenUiState) {
        ReceiveUiState.Advertising -> SearchingScreen(viewModel = viewModel
        /*todo
           better pass individual method references like ::viewModel.startSearching (?)*/)
        is ReceiveUiState.ShowingUsersList -> ShowingUsersListScreen(viewModel = viewModel)
        ReceiveUiState.Connecting -> ConnectingScreen(viewModel = viewModel)
        ReceiveUiState.Connected -> ConnectedScreen(viewModel = viewModel)
        ReceiveUiState.Accepted -> AcceptedScreen(viewModel = viewModel)
        ReceiveUiState.Rejected -> RejectedScreen(viewModel = viewModel)
        ReceiveUiState.Receiving -> ReceivingScreen(viewModel = viewModel)
        ReceiveUiState.Success -> SuccessScreen(viewModel = viewModel)
        ReceiveUiState.Interrupted -> InterruptedScreen(viewModel = viewModel)
    }

}

@Composable
private fun SearchingScreen(viewModel: ReceiveViewModelNew) {

}

@Composable
private fun ShowingUsersListScreen(viewModel: ReceiveViewModelNew) {

}

@Composable
private fun ConnectingScreen(viewModel: ReceiveViewModelNew) {

}

@Composable
private fun ConnectedScreen(viewModel: ReceiveViewModelNew) {

}

@Composable
private fun AcceptedScreen(viewModel: ReceiveViewModelNew) {

}

@Composable
private fun RejectedScreen(viewModel: ReceiveViewModelNew) {

}

@Composable
private fun ReceivingScreen(viewModel: ReceiveViewModelNew) {

}

@Composable
private fun SuccessScreen(viewModel: ReceiveViewModelNew) {

}

@Composable
private fun InterruptedScreen(viewModel: ReceiveViewModelNew) {

}

