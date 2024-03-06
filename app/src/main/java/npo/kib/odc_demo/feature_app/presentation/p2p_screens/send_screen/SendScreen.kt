package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.SharedFlow
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.CustomBluetoothDevice
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionDataBuffer
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionStatus.SenderTransactionStatus
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionStatus.SenderTransactionStatus.*
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.CustomSnackbar
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.ODCGradientActionButton
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.common.components.*
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen.SendUiState.*
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen.SendUiState.OperationResult.ResultType.Failure
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen.SendUiState.OperationResult.ResultType.Success
import npo.kib.odc_demo.ui.GradientColors

@Composable
fun SendRoute(
    navigateToP2PRoot: () -> Unit,
    viewModel: SendViewModel
) {
    //todo manage permissions first
    val sendScreenState by viewModel.state.collectAsStateWithLifecycle()
    SendScreen(
        navigateToP2PRoot = navigateToP2PRoot,
        screenState = sendScreenState,
        errorsFlow = viewModel.errors,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun SendScreen(
    navigateToP2PRoot: () -> Unit, //todo use in a separate button which would be inactive on unsafe steps
    screenState: SendScreenState,
    errorsFlow : SharedFlow<String>,
    onEvent: (SendScreenEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val isScreenLabelVisible = remember { MutableTransitionState(false) }.apply { targetState = true }
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            visibleState = isScreenLabelVisible,
            enter = slideInVertically(),
            exit = slideOutVertically()
        ) {
            Text(
                text = "You can send banknotes on this screen",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .requiredHeight(50.dp)
            )
        }
        with(SendScreenSubScreens) {
            AnimatedContent(
                modifier = Modifier,
                targetState = screenState,
                contentKey = { it.uiState },
                label = "ReceiveScreenAnimatedContent",
                transitionSpec = {
                    fadeIn(tween(1000, 0)) togetherWith
                            fadeOut(tween(1000, 0))
                },
                contentAlignment = Alignment.Center,
            ) { state ->
                when (state.uiState) {
                    Initial -> InitialScreen(onClickStartSearching = { onEvent(SendScreenEvent.SetDiscovering(true)) })
                    is Discovering -> SearchingScreen(foundDevices = screenState.bluetoothState.scannedDevices,
                        onUserClicked = { device -> onEvent(SendScreenEvent.ConnectToDevice(device = device)) },
                        onClickCancelSearching = { onEvent(SendScreenEvent.SetDiscovering(false)) })
                    Loading -> InProgressScreen(label = "Connecting...")
                    is InTransaction -> TransactionBlock(
                        dataBuffer = screenState.transactionDataBuffer,
                        transactionStatus = state.uiState.status,
                        onEvent = onEvent
                    )
                    is OperationResult -> when (state.uiState.result) {
                        is Failure -> FailureScreen(onClickRetry = {})
                        Success -> SuccessScreen()
                    }
                }
            }
        }
        val snackbarHostState = remember { SnackbarHostState() }
        LaunchedEffect(key1 = true) {
            errorsFlow.collect { error ->
                snackbarHostState.showSnackbar(message = "Error:\n$error")
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.fillMaxWidth(0.8f).requiredHeight(80.dp)
        ){ snackbarData ->
            CustomSnackbar(
                snackbarData,
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(40.dp)
                    .padding(horizontal = 30.dp, vertical = 0.dp),
                textColor = Color.White.copy(alpha = 0.8f),
                surfaceColor = Color.DarkGray.copy(alpha = 0.5f),
                borderColor = Color.Transparent
            )
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
//        bondedDevices : List<CustomBluetoothDevice>, //todo show bondedDevices at the top
        foundDevices: List<CustomBluetoothDevice>,
        onUserClicked: (CustomBluetoothDevice) -> Unit,
        onClickCancelSearching: () -> Unit
    ) {
        Column {
            Button(onClick = onClickCancelSearching) {
                Text(text = "Cancel searching")
            }
            UsersList(
                deviceList = foundDevices,
                onClickDevice = onUserClicked
            )
        }
    }

    @Composable
    fun InProgressScreen(label: String) {
        Column {
            Text(text = label)
            CircularProgressIndicator()
        }
    }

    context(AnimatedContentScope)
    @Composable
    fun TransactionBlock(
        modifier: Modifier = Modifier,
        dataBuffer: TransactionDataBuffer,
        transactionStatus: SenderTransactionStatus,
        onEvent: (SendScreenEvent) -> Unit
    ) {
        Column(modifier = modifier) {
                when (transactionStatus) {
                    INITIAL -> AmountSelectionScreen(dataBuffer = dataBuffer,
                        onClickTryConstructAmount = { amount -> onEvent(SendScreenEvent.TryConstructAmount(amount)) })
                    CONSTRUCTING_AMOUNT -> InProgressScreen(label = "Constructing amount...")
                    SHOWING_AMOUNT_AVAILABILITY -> {}
                    WAITING_FOR_OFFER_RESPONSE -> InProgressScreen(label = "Waiting for reaction to the offer...")
                    OFFER_ACCEPTED -> StatusInfoBlock(statusLabel = "Offer accepted!")
                    OFFER_REJECTED -> OfferRejectedScreen(onClickSendAnotherOffer = { /*TODO*/ }) {
                        //todo here should return to constructing amount composable
                    }
                    SENDING_BANKNOTES_LIST -> StatusInfoBlock(statusLabel = "Sending banknotes...")
                    WAITING_FOR_BANKNOTES_RECEIVED_RESPONSE -> StatusInfoBlock(statusLabel = "Waiting for \"banknotes received\" response.")
                    WAITING_FOR_ACCEPTANCE_BLOCKS -> StatusInfoBlock(statusLabel = "Waiting for acceptance blocks...",
                        infoText = "Processing banknote # ${dataBuffer.currentlyProcessedBanknoteOrdinal + 1}"
                    )
                    SIGNING_SENDING_NEW_BLOCK -> StatusInfoBlock(statusLabel = "Signing and sending new block...",
                        infoText = "Processing banknote # ${dataBuffer.currentlyProcessedBanknoteOrdinal + 1}"
                    )
                    ALL_BANKNOTES_PROCESSED -> StatusInfoBlock(statusLabel = "All banknotes are processed!",
                        infoText = "Waiting for the success confirmation...")
                    DELETING_BANKNOTES_FROM_WALLET -> StatusInfoBlock(statusLabel = "Deleting banknotes from the wallet...")
                    BANKNOTES_DELETED -> StatusInfoBlock(statusLabel = "Sent banknotes are removed from the wallet..." )
                    FINISHED_SUCCESSFULLY -> { /* TransactionBlock is not visible here,
                    ui state is automatically set to OperationResult Success in the viewmodel.*/
                    }
                    ERROR -> {/* TransactionBlock is not visible here,
                    ui state is automatically set to OperationResult Failure in the viewmodel.*/
                    }
                }
            }
            //todo maybe make visibly active only on safe steps, save safe steps in a list
            ODCGradientActionButton(text = "Disconnect",
                gradientColors = GradientColors.ButtonNegativeActionColors,
                onClick = { onEvent(SendScreenEvent.Disconnect) })
        }



    /**
     *  A field to enter the amount and a button to try to construct it.
     *  Check if the amount can be constructed with the available banknotes.
     *  If the amount can not be constructed, suggest closest possible amount (this feature will be added later).
     * */
    context(AnimatedVisibilityScope)
    @Composable
    fun AmountSelectionScreen(
        dataBuffer: TransactionDataBuffer,
        onClickTryConstructAmount: (Int) -> Unit
    ) {
        Column {
            Text(text = "Connected to user")
            UserInfoBlock(userInfo = dataBuffer.otherUserInfo)
            //todo TextFieldWithHint(){}
            Button(onClick = { } /*onClickTryConstructAmount(with the entered amount)
            make a fast check if a valid number is entered*/) {
                Text(text = "try constructing the amount")
            }
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

context(AnimatedContentScope)
@Composable
fun StatusInfoBlock(
    statusLabel: String,
    infoText : String? = null
) {
    Column {
        Text(text = statusLabel, modifier = Modifier.animateFadeVerticalSlideInOut())
        infoText?.let { Text(text = it, modifier = Modifier.animateFadeVerticalSlideInOut(enterDelay = 100))}
    }
}

}