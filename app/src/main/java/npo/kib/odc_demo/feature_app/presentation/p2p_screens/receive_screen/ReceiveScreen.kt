package npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen

import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import npo.kib.odc_demo.feature_app.data.permissions.PermissionProvider.LocalAppBluetoothPermissions
import npo.kib.odc_demo.feature_app.data.permissions.getTextToShowGivenPermissions
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.UserInfo
import npo.kib.odc_demo.feature_app.domain.transaction_logic.ReceiverTransactionStatus
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionDataBuffer
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.MultiplePermissionsRequestBlock
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.ODCGradientActionButton
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.common.components.UserInfoBlock
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveScreenSubScreens.ResultScreen
import npo.kib.odc_demo.ui.GradientColors
import npo.kib.odc_demo.ui.theme.ODCAppTheme

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReceiveRoute(
    navBackStackEntry: NavBackStackEntry
) {
    val multiplePermissionsState =
        rememberMultiplePermissionsState(permissions = LocalAppBluetoothPermissions.current)
    if (multiplePermissionsState.allPermissionsGranted) {
//    If all the required bluetooth permissions are granted, the viewmodel is created and ReceiveScreen() is launched

        val registry = LocalActivityResultRegistryOwner.current!!.activityResultRegistry
        val receiveViewModel: ReceiveViewModel =
            hiltViewModel(viewModelStoreOwner = navBackStackEntry,
                creationCallback = { factory: ReceiveViewModel.Companion.ReceiveViewModelFactory ->
                    factory.create(registry)
                })
        val receiveScreenState by receiveViewModel.state.collectAsStateWithLifecycle()
//        val receiveScreenState = ReceiveScreenState()
        ReceiveScreen(
            screenState = receiveScreenState,
            onEvent = receiveViewModel::onEvent
        )
    } else {
        MultiplePermissionsRequestBlock(permissionsRequestText = getTextToShowGivenPermissions(
            multiplePermissionsState.revokedPermissions,
            multiplePermissionsState.shouldShowRationale,
        ),
            onRequestPermissionsClick = { multiplePermissionsState.launchMultiplePermissionRequest() })
    }

}


@Composable
private fun ReceiveScreen(
    screenState: ReceiveScreenState,
    onEvent: (ReceiveScreenEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val isScreenLabelVisible by remember { mutableStateOf(true) }
        AnimatedVisibility(
            visible = isScreenLabelVisible,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Text(
                text = "Receive banknotes",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        with(ReceiveScreenSubScreens) {
            when (screenState.uiState) {
                ReceiveUiState.Initial -> InitialScreen(onClickStartAdvertising = {
                    onEvent(
                        ReceiveScreenEvent.SetAdvertising(true)
                    )
                })
                ReceiveUiState.Advertising -> AdvertisingScreen(onClickStopAdvertising = {
                    onEvent(ReceiveScreenEvent.SetAdvertising(false))
                })
                ReceiveUiState.Loading -> CircularProgressIndicator()
                is ReceiveUiState.Connected -> ConnectedScreen(screenState.transactionDataBuffer.otherUserInfo,
                    onClickDisconnect = { onEvent(ReceiveScreenEvent.Disconnect) })


                is ReceiveUiState.InTransaction -> TransactionBlock(dataBuffer = screenState.transactionDataBuffer, status = screenState.uiState.status, )
//                is ReceiveUiState.OfferReceived -> OfferReceivedScreen(amount = screenState.transactionDataBuffer.amountRequest?.amount,
//                    fromUser = screenState.transactionDataBuffer.otherUserInfo,
//                    onClickAccept = { onEvent(ReceiveScreenEvent.ReactToOffer(accept = true)) },
//                    onClickReject = { onEvent(ReceiveScreenEvent.ReactToOffer(accept = false)) })

//                ReceiveUiState.ReceivingAllBanknotes -> ReceivingAllBanknotesScreen()
//                ReceiveUiState.ProcessingBanknote -> ProcessingBanknoteScreen(banknoteId = -1)

                is ReceiveUiState.OperationResult -> when (screenState.uiState.result) {
                    is ReceiveUiState.OperationResult.ResultType.Failure -> FailureScreen(onClickRetry = {})

                    ReceiveUiState.OperationResult.ResultType.Success -> SuccessScreen(onClickFinish = {})
                }
            }
        }
    }
}

private object ReceiveScreenSubScreens {
    @Composable
    fun InitialScreen(onClickStartAdvertising: () -> Unit) {
        Column {
            Text(text = "Initial screen")
            Button(onClick = onClickStartAdvertising) {
                Text(text = "Start advertising")
            }
        }
    }

    @Composable
    fun AdvertisingScreen(onClickStopAdvertising: () -> Unit) {
        Column {
            Text(text = "Advertising screen")
            Button(onClick = onClickStopAdvertising) {
                Text(text = "Stop advertising")
            }
        }
    }

    //todo do crossfade
    //use my ComposeTestingStuff project as a reference
    @Composable
    fun TransactionBlock(dataBuffer: TransactionDataBuffer, status: ReceiverTransactionStatus, onEvent: (ReceiveScreenEvent) -> Unit) {
        when(status){
            ReceiverTransactionStatus.WAITING_FOR_OFFER -> ConnectedScreen(dataBuffer.otherUserInfo,
                onClickDisconnect = { onEvent(ReceiveScreenEvent.Disconnect) })
            ReceiverTransactionStatus.OFFER_RECEIVED -> OfferReceivedScreen(
                amount = dataBuffer.amountRequest?.amount,
                fromUser = dataBuffer.otherUserInfo,
                onClickAccept = { onEvent(ReceiveScreenEvent.ReactToOffer(accept = true)) },
                onClickReject = { onEvent(ReceiveScreenEvent.ReactToOffer(accept = false)) })
            ReceiverTransactionStatus.RECEIVING_BANKNOTES_LIST -> InProgressScreen(label = "Receiving banknotes list...")
            ReceiverTransactionStatus.BANKNOTES_LIST_RECEIVED -> {}
            ReceiverTransactionStatus.CREATING_SENDING_ACCEPTANCE_BLOCKS -> TODO()
            ReceiverTransactionStatus.WAITING_FOR_SIGNED_BLOCK -> TODO()
            ReceiverTransactionStatus.VERIFYING_RECEIVED_BLOCK -> TODO()
            ReceiverTransactionStatus.ALL_BANKNOTES_VERIFIED -> TODO()
            ReceiverTransactionStatus.SAVING_BANKNOTES_TO_WALLET -> TODO()
            ReceiverTransactionStatus.BANKNOTES_SAVED -> TODO()
            ReceiverTransactionStatus.FINISHED_SUCCESSFULLY -> TODO()
            ReceiverTransactionStatus.WAITING_FOR_ANY_RESPONSE -> TODO()
            ReceiverTransactionStatus.ERROR -> TODO()
        }
    }

    @Composable
    fun InProgressScreen(label: String) {
        Column {
            Text(text = label)
            CircularProgressIndicator()
        }
    }

    @Composable
    fun SlowFadingInOutBlock(content: @Composable () -> Unit) {
        var visible by remember { mutableStateOf(true) }
        val v2 by rememberUpdatedState(newValue = true)
        val v3 by remember {
            derivedStateOf { true }
        }
        LaunchedEffect(key1 = ) {
            
        }
        DisposableEffect(key1 = ) {
            
        }
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
           content()
        }
    }

    @Composable
    fun ConnectedScreen(
        otherUserInfo: UserInfo?,
        onClickDisconnect: () -> Unit
    ) {
        Column {
            Text(text = "Connected to user:")
            Spacer(modifier = Modifier.height(5.dp))
            UserInfoBlock(userInfo = otherUserInfo)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "Waiting for offer...") //todo add dots animation
            Spacer(modifier = Modifier.height(5.dp))
            ODCGradientActionButton(
                modifier = Modifier.fillMaxSize(),
                text = "Disconnect",
                onClick = onClickDisconnect
            )
        }
    }
    @Composable
    fun OfferReceivedScreen(
        amount: Int?,
        fromUser: UserInfo?,
        onClickAccept: () -> Unit,
        onClickReject: () -> Unit
    ) {
        Surface {
            Column {
                Text(text = "Offer received from: ")
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = fromUser?.userName ?: "null")
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "Wallet ID:")
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = fromUser?.walletId ?: "null")
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "Amount:")
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "$amount RUB")
                Column {
                    ODCGradientActionButton(
                        text = "Accept offer",
                        gradientColors = GradientColors.ButtonPositiveActionColors,
                        onClick = onClickAccept
                    )
                    ODCGradientActionButton(
                        text = "Reject offer",
                        gradientColors = GradientColors.ButtonNegativeActionColors,
                        onClick = onClickReject
                    )
                }
            }
        }
    }

    @Composable
    fun ReceivingAllBanknotesScreen() {
        Text(text = "Receiving banknotes...")
    }


    //todo later pass some other valuable banknote info
    @Composable
    fun ProcessingBanknoteScreen(banknoteId: Int) {
        Column {
            Text(text = "All banknotes sent successfully, verifying...")
            //todo screen for receiving for each individual banknote verification status
            // (sent acceptance blocks (init), waiting for signature, verified signature,
            // then the same for the next banknote...
            Text(text = "Processing banknote number: $banknoteId")
            Text(text = "Status: signing...")
        }
    }

    @Composable
    fun ResultScreen(result: ReceiveUiState.OperationResult.ResultType) {
        when (result) {
            ReceiveUiState.OperationResult.ResultType.Success -> Column {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Receiving success!",
                    color = Color.Green
                )
            }

            is ReceiveUiState.OperationResult.ResultType.Failure -> Column {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Failure: \n${result.failureMessage}",
                    color = Color.Red
                )
            }
        }
    }

    @Composable
    fun SuccessScreen(onClickFinish: () -> Unit) {
        Column {
            Text(text = "Transaction successful")
            ODCGradientActionButton(
                text = "Finish",
                gradientColors = GradientColors.ColorSet2,
                onClick = onClickFinish
            )
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

@Preview
@Composable
private fun ResultScreenPreviews() {
    ODCAppTheme {
        Column {
            ResultScreen(result = ReceiveUiState.OperationResult.ResultType.Success)
            ResultScreen(result = ReceiveUiState.OperationResult.ResultType.Failure("Some error"))
        }
    }
}