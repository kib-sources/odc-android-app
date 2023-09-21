package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.p2p_screens.receive_screen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ReceiveScreen(viewModel: ReceiveViewModelNew = hiltViewModel()
) {
    val receiveScreenUiState: ReceiveUiState = viewModel.receiveUiState

    when (receiveScreenUiState) {
        ReceiveUiState.Advertising -> SearchingScreen(viewModel = viewModel)
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

