package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.data.permissions.PermissionProvider
import npo.kib.odc_demo.feature_app.data.permissions.getTextToShowGivenPermissions
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.CustomBluetoothDevice
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionDataBuffer
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionStatus.SenderTransactionStatus
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionStatus.SenderTransactionStatus.*
import npo.kib.odc_demo.feature_app.domain.util.containsPrefix
import npo.kib.odc_demo.feature_app.domain.util.isAValidAmount
import npo.kib.odc_demo.feature_app.domain.util.withoutPrefix
import npo.kib.odc_demo.feature_app.presentation.common.components.CustomSnackbar
import npo.kib.odc_demo.feature_app.presentation.common.components.MultiplePermissionsRequestBlock
import npo.kib.odc_demo.feature_app.presentation.common.components.ODCGradientButton
import npo.kib.odc_demo.feature_app.presentation.common.components.TransparentHintTextField
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.common.components.StatusInfoBlock
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.common.components.UserInfoBlock
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.common.components.UsersList
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.common.components.animateFadeVerticalSlideInOut
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen.SendUiState.*
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen.SendUiState.OperationResult.ResultType.Failure
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen.SendUiState.OperationResult.ResultType.Success
import npo.kib.odc_demo.ui.GradientColors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SendRoute(
    navigateToP2PRoot: () -> Unit, navBackStackEntry: NavBackStackEntry
) {
    val multiplePermissionsState =
        rememberMultiplePermissionsState(permissions = PermissionProvider.LocalAppBluetoothPermissions.current)
    if (multiplePermissionsState.allPermissionsGranted) {
        val viewModel = hiltViewModel<SendViewModel>(viewModelStoreOwner = navBackStackEntry)
        val sendScreenState by viewModel.state.collectAsStateWithLifecycle()
        SendScreen(
            navigateToP2PRoot = navigateToP2PRoot,
            screenState = sendScreenState,
            errorsFlow = viewModel.errors,
            onEvent = viewModel::onEvent
        )
    } else {
        LaunchedEffect(key1 = multiplePermissionsState.allPermissionsGranted) {
            launch { navBackStackEntry.viewModelStore.clear() }
        }
        MultiplePermissionsRequestBlock(permissionsRequestText = getTextToShowGivenPermissions(
            multiplePermissionsState.revokedPermissions,
            multiplePermissionsState.shouldShowRationale,
        ),
            onRequestPermissionsClick = { multiplePermissionsState.launchMultiplePermissionRequest() })
    }
}

@Composable
private fun SendScreen(
    navigateToP2PRoot: () -> Unit, //todo use in a separate button which would be inactive on unsafe steps
    screenState: SendScreenState, errorsFlow: SharedFlow<String>, onEvent: (SendScreenEvent) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 5.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val isScreenLabelVisible =
            remember { MutableTransitionState(false) }.apply { targetState = true }
        AnimatedVisibility(
            modifier = Modifier
                .requiredHeight(50.dp)
                .fillMaxWidth(),
            visibleState = isScreenLabelVisible,
            enter = slideInVertically(),
            exit = slideOutVertically()
        ) {
            Text(
                text = "You can send banknotes on this screen",
                modifier = Modifier.requiredHeight(50.dp),
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.Underline
            )
        }
        with(SendScreenSubScreens) {
            AnimatedContent(
                modifier = Modifier.fillMaxWidth(),
                targetState = screenState,
                contentKey = { it.uiState },
                label = "SendScreenAnimatedContent",
                transitionSpec = {
                    fadeIn(tween(1000, 0)) togetherWith fadeOut(tween(1000, 0))
                },
                contentAlignment = Alignment.Center,
            ) { state ->
                when (val uiState = state.uiState) {
                    Initial -> InitialScreen(onClickStartSearching = {
                        onEvent(
                            SendScreenEvent.SetDiscovering(
                                true
                            )
                        )
                    })

                    is Discovering -> SearchingScreen(foundDevices = screenState.bluetoothState.scannedDevices,
                        onUserClicked = { device -> onEvent(SendScreenEvent.ConnectToDevice(device = device)) },
                        onClickCancelSearching = { onEvent(SendScreenEvent.SetDiscovering(false)) })

                    Loading -> InProgressScreen(label = "Connecting...")

                    is InTransaction -> TransactionBlock(
                        dataBuffer = screenState.transactionDataBuffer,
                        transactionStatus = uiState.status,
                        onEvent = onEvent
                    )

                    is OperationResult -> when (val result = uiState.result) {
                        is Failure -> FailureScreen(
                            failureMessage = result.failureMessage, onClickAbort = navigateToP2PRoot
                        )

                        Success -> SuccessScreen(onClickFinish = navigateToP2PRoot)
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
        SnackbarHost( //todo extract to common package
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .requiredHeightIn(min = 80.dp, max = 250.dp)
        ) { snackbarData ->
            CustomSnackbar(
                snackbarData,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
//                    .requiredHeight(40.dp)
                    .padding(
                        horizontal = 30.dp, vertical = 0.dp
                    ),
                textColor = Color.White.copy(alpha = 0.8f),
                surfaceColor = Color.DarkGray.copy(alpha = 0.5f),
                borderColor = Color.Transparent
            )
        }
    }
}

private object SendScreenSubScreens {

    context(AnimatedVisibilityScope)
    @Composable
    fun InitialScreen(onClickStartSearching: () -> Unit) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(text = "Search for users", Modifier.animateFadeVerticalSlideInOut())
            Spacer(modifier = Modifier.height(15.dp))
            ODCGradientButton(text = "Start searching", onClick = onClickStartSearching)
        }
    }

    @Composable
    fun SearchingScreen(
//        bondedDevices : List<CustomBluetoothDevice>,
        foundDevices: List<CustomBluetoothDevice>,
        onUserClicked: (CustomBluetoothDevice) -> Unit,
        onClickCancelSearching: () -> Unit
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            ODCGradientButton(
                text = "Cancel searching",
                onClick = onClickCancelSearching,
                gradientColors = GradientColors.ButtonNegativeActionColors
            )
            UsersList(deviceList = foundDevices.filter { it.name.containsPrefix() }
                .map { it.copy(name = it.name!!.withoutPrefix()) },
                showAddresses = false,
                onClickDevice = onUserClicked
            )
        }
    }

    context(AnimatedVisibilityScope)
    @Composable
    fun InProgressScreen(label: String) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StatusInfoBlock(statusLabel = label)
        }
    }

    context(AnimatedVisibilityScope)
    @Composable
    fun TransactionBlock(
        dataBuffer: TransactionDataBuffer,
        transactionStatus: SenderTransactionStatus,
        onEvent: (SendScreenEvent) -> Unit
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UserInfoBlock(
                modifier = Modifier.animateFadeVerticalSlideInOut(),
                userInfo = dataBuffer.otherUserInfo
            )
            when (transactionStatus) {
                INITIAL, SHOWING_AMOUNT_AVAILABILITY, CONSTRUCTING_AMOUNT, OFFER_REJECTED -> AmountSelectionScreen(
                    status = transactionStatus,
                    dataBuffer = dataBuffer,
                    onClickTryConstructAmount = { amount ->
                        onEvent(SendScreenEvent.TryConstructAmount(amount))
                    },
                    onClickSendOffer = { onEvent(SendScreenEvent.TrySendOffer) })

                WAITING_FOR_OFFER_RESPONSE -> InProgressScreen(label = "Waiting for an answer to the offer...")
                OFFER_ACCEPTED -> StatusInfoBlock(statusLabel = "The offer was accepted!")
                SENDING_BANKNOTES_LIST -> StatusInfoBlock(statusLabel = "Sending banknotes...")
                WAITING_FOR_BANKNOTES_RECEIVED_RESPONSE -> StatusInfoBlock(statusLabel = "Waiting for \"banknotes received\" response.")
                WAITING_FOR_ACCEPTANCE_BLOCKS -> StatusInfoBlock(
                    statusLabel = "Waiting for acceptance blocks...",
                    infoText = "Processing banknote # ${dataBuffer.currentlyProcessedBanknoteOrdinal + 1}"
                )

                SIGNING_SENDING_NEW_BLOCK -> StatusInfoBlock(
                    statusLabel = "Signing and sending new block...",
                    infoText = "Processing banknote # ${dataBuffer.currentlyProcessedBanknoteOrdinal + 1}"
                )

                ALL_BANKNOTES_PROCESSED -> StatusInfoBlock(
                    statusLabel = "All banknotes are processed!",
                    infoText = "Waiting for the success confirmation..."
                )

                DELETING_BANKNOTES_FROM_WALLET -> StatusInfoBlock(statusLabel = "Deleting banknotes from the wallet...")
                BANKNOTES_DELETED -> StatusInfoBlock(statusLabel = "Sent banknotes are removed from the wallet...")
                FINISHED_SUCCESSFULLY -> { /* TransactionBlock is not visible here,
                    ui state is automatically set to OperationResult Success in the viewmodel.*/
                }

                ERROR -> {/* TransactionBlock is not visible here,
                    ui state is automatically set to OperationResult Failure in the viewmodel.*/
                }
            }
            //todo maybe keep visible only on safe steps, store safe steps as a list
            //fixme currently crashes the app
            //fixme now is hidden behind bottom navigation bar
//            Text(
//                text = "Disconnect",
//                color = MaterialTheme.colorScheme.onBackground,
//                modifier = Modifier.clickable {
//                    onEvent(SendScreenEvent.Disconnect)
//                })
        }
    }


    /**
     *  A field to enter the amount and a button to try to construct it.
     *  Check if the amount can be constructed with the available banknotes.
     *  If the amount can not be constructed, suggest closest possible amount (this feature will be added later).
     * */
    context(AnimatedVisibilityScope)
    @Composable
    fun AmountSelectionScreen(
        status: SenderTransactionStatus,
        dataBuffer: TransactionDataBuffer,
        onClickTryConstructAmount: (Int) -> Unit,
        onClickSendOffer: () -> Unit
    ) {
        var amountText by rememberSaveable {
            mutableStateOf("")
        }
        val amountIsValid by remember {
            derivedStateOf { amountText.isAValidAmount() }
        }
        val isAmountAvailable by remember {
            derivedStateOf { dataBuffer.isAmountAvailable == true }
        }
        val shouldShowTextInput: Boolean by remember {
            derivedStateOf {
                when {
                    status == INITIAL -> true
                    status == SHOWING_AMOUNT_AVAILABILITY && !isAmountAvailable -> true
                    status == OFFER_REJECTED -> true
                    else -> false
                }
            }
        }
        Surface(
            modifier = Modifier.animateContentSize(), color = MaterialTheme.colorScheme.background
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 5.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //First part with status label
                when (status) {
                    INITIAL -> StatusInfoBlock(statusLabel = "Enter the amount to send")
                    CONSTRUCTING_AMOUNT -> InProgressScreen(label = "Constructing amount...")
                    SHOWING_AMOUNT_AVAILABILITY -> {
                        StatusInfoBlock(
                            statusLabel = "The amount is ${if (isAmountAvailable) "available!" else "not available, try again"}"
                        )
                        if (isAmountAvailable) ODCGradientButton(
                            text = "Send the offer", onClick = onClickSendOffer
                        )
                    }

                    OFFER_REJECTED -> StatusInfoBlock(
                        statusLabel = "The offer was rejected", infoText = "Try again or disconnect"
                    )

                    else -> {/* not encountered on this screen */
                    }
                }
                //Second part with the text field and button
                if (shouldShowTextInput) {
                    TransparentHintTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeight(50.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(Dp.Hairline, color = MaterialTheme.colorScheme.onBackground)
                            .padding(15.dp),
                        hint = "Enter a valid amount...",
                        onValueChange = { amountText = it },
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    ODCGradientButton(text = if (amountIsValid) "Construct the amount" else "Invalid amount entered",
                        enabled = amountIsValid,
                        gradientColors = if (amountIsValid) GradientColors.ButtonPositiveActionColors else GradientColors.ButtonNegativeActionColors,
                        onClick = { if (amountIsValid) onClickTryConstructAmount(amountText.toInt()) })
                }
            }
        }
    }

    @Composable
    fun SuccessScreen(onClickFinish: () -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Transaction finished successfully")
            ODCGradientButton(
                text = "Finish", onClick = onClickFinish
            )
        }
    }

    @Composable
    fun FailureScreen(
        failureMessage: String, onClickAbort: () -> Unit
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "An error has occurred:")
            Text(text = failureMessage)
            ODCGradientButton(
                text = "Abort transaction",
                gradientColors = GradientColors.ButtonNegativeActionColors,
                onClick = onClickAbort
            )
        }
    }

}