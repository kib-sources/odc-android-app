package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.CustomBluetoothDevice
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.common.components.CancelTransactionBlock
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.common.components.UsersList
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveUiState.ResultType.*

@Composable
fun SendRoute(
    viewModel: SendViewModelNew
) {

    //todo change uistate type to SendScreenState
    val sendUiState by viewModel.uiState.collectAsStateWithLifecycle()

    SendScreen(uiState = sendUiState, onEvent = viewModel::onEvent)

}

@Composable
private fun SendScreen(
    uiState: SendUiState,
    onEvent: (SendScreenEvent) -> Unit
) {

    Column(modifier = Modifier.fillMaxSize()) {
        val isScreenLabelVisible by remember { mutableStateOf(true) }
        AnimatedVisibility(
            visible = isScreenLabelVisible,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Text(text = "Send banknotes here!", fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        with(SendScreenSubScreens) {
            when (uiState) {
                SendUiState.Initial -> InitialScreen(onClickStartSearching = { onEvent(SendScreenEvent.StartSearching) })
                is SendUiState.Searching -> SearchingScreen(users = uiState.usersList,
                    onUserClicked = { device -> onEvent(SendScreenEvent.ConnectToUser(device = device)) },
                    onClickReset = { onEvent(SendScreenEvent.Reset) })

                SendUiState.Connecting -> ConnectingScreen()
                SendUiState.ConnectionRejected -> ConnectionRejectedScreen(onClickSearchAgain = {
                    onEvent(
                        SendScreenEvent.StartSearching
                    )
                })

                SendUiState.Connected -> ConnectedScreen()
                is SendUiState.OfferSent -> WaitingForOfferAcceptance()
                is SendUiState.OfferResponse -> when (uiState.isAccepted) {
                    false -> OfferRejectedScreen(onClickSendAnotherOffer = { onEvent(SendScreenEvent.StartSearching) },
                        onClickReset = { onEvent(SendScreenEvent.Reset) })

                    true -> SendingAllBanknotesScreen()
                }

                is SendUiState.ProcessingBanknote -> ProcessingBanknoteScreen(banknoteId = uiState.banknoteId)
                is SendUiState.Result -> when (uiState.result) {
                    is Failure -> FailureScreen(onClickRetry = {})
                    Success -> SuccessScreen()
                }
            }
        }
        AnimatedVisibility(
            visible = ((uiState !is SendUiState.Initial) && (uiState != SendUiState.Result(Success))),
            //todo animate sliding from bottom to top
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            CancelTransactionBlock(onCancelClick = { onEvent(SendScreenEvent.Reset) })
        }
    }
}

private object SendScreenSubScreens {
    @Composable
    fun InitialScreen(onClickStartSearching: () -> Unit) {
        Column {
            Text(text = "Initial Screen")
            Button(onClick = onClickStartSearching) {
                Text(text = "Start searching")
            }
        }
    }

    @Composable
    fun SearchingScreen(
        users: List<CustomBluetoothDevice>,
        onUserClicked: (CustomBluetoothDevice) -> Unit,
        onClickReset: () -> Unit
    ) {
        Column {
            Button(onClick = onClickReset) {
                Text(text = "Cancel")
            }
            UsersList(deviceList = users, onClickDevice = onUserClicked)
        }
    }

    @Composable
    fun ConnectingScreen() {
        Text(text = "Connecting...")
    }

    @Composable
    fun ConnectionRejectedScreen(onClickSearchAgain: () -> Unit) {
        Column {
            Text(text = "The user rejected the connection")
            Button(onClick = onClickSearchAgain) {
                Text(text = "Search again")
            }
        }
    }

    /**
     *  A field to enter amount and a button to send offer here.
     *  Check if the amount can be constructed with available banknotes.
     *  Example: if there are banknotes of 50 and 100 RUB on device,
     *  the user can only send 50, 100 or 150 RUB, when choosing the other amount
     *  tell the user that the amount cannot be sent. Suggest closest possible amount.
     * */
    @Composable
    fun ConnectedScreen(onClickSendOffer: () -> Unit = {}) {
        Column {
            Text(text = "Connected to user")
            //todo TextFieldWithHint(){}
            Button(onClick = onClickSendOffer) {
                Text(text = "Send the amount")
            }
        }
    }


    @Composable
    fun WaitingForOfferAcceptance() {
        Column {
            Text(text = "Waiting for other user to react to offer")
        }
    }

    @Composable
    fun OfferRejectedScreen(
        onClickSendAnotherOffer: () -> Unit,
        onClickReset: () -> Unit
    ) {
        Column {
            Text(text = "The user rejected the offer")
            Button(onClick = onClickSendAnotherOffer) {
                Text(text = "Send another offer")
            }
            Button(onClick = onClickReset) {
                Text(text = "Cancel the transaction")
            }
        }
    }

    @Composable
    fun SendingAllBanknotesScreen() {
        Column {
            Text(text = "Offer accepted!")
            Text(text = "Sending all banknotes...")
        }
    }

    //todo later pass some other valuable banknote info
    @Composable
    fun ProcessingBanknoteScreen(banknoteId: Int) {
        Column {
            Text(text = "All banknotes sent successfully, verifying...")
            //todo screen for sending for each individual banknote verification status
            Text(text = "Processing banknote number: $banknoteId")
            Text(text = "Status: signing...")
        }
    }

    @Composable
    fun SuccessScreen() {
        Column {
            Text(text = "Transaction successful")
        }
    }

    @Composable
    fun FailureScreen(onClickRetry: () -> Unit) {
        Column {
            Text(text = "Something went wrong, try again?")
            Button(onClick = onClickRetry) {
                Text(text = "Retry")
            }
        }
    }

}