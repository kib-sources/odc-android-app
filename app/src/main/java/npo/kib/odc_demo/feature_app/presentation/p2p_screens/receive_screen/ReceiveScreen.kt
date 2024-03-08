package npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen

import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
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
import kotlinx.coroutines.flow.SharedFlow
import npo.kib.odc_demo.feature_app.data.permissions.PermissionProvider.LocalAppBluetoothPermissions
import npo.kib.odc_demo.feature_app.data.permissions.getTextToShowGivenPermissions
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.UserInfo
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionDataBuffer
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionStatus.ReceiverTransactionStatus
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionStatus.ReceiverTransactionStatus.*
import npo.kib.odc_demo.feature_app.presentation.common.components.CustomSnackbar
import npo.kib.odc_demo.feature_app.presentation.common.components.MultiplePermissionsRequestBlock
import npo.kib.odc_demo.feature_app.presentation.common.components.ODCGradientActionButton
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.common.components.*
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveUiState.*
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveUiState.OperationResult.ResultType.*
import npo.kib.odc_demo.ui.GradientColors
import npo.kib.odc_demo.ui.theme.ODCAppTheme

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReceiveRoute(
    navigateToP2PRoot: () -> Unit,
    navBackStackEntry: NavBackStackEntry
) {
    val multiplePermissionsState =
        rememberMultiplePermissionsState(permissions = LocalAppBluetoothPermissions.current)
    if (multiplePermissionsState.allPermissionsGranted) {
//      If all the required bluetooth permissions are granted, the viewmodel is created and ReceiveScreen() is launched
        val registry = LocalActivityResultRegistryOwner.current!!.activityResultRegistry
        val receiveViewModel: ReceiveViewModel =
            hiltViewModel(viewModelStoreOwner = navBackStackEntry,
                creationCallback = { factory: ReceiveViewModel.Companion.ReceiveViewModelFactory ->
                    factory.create(registry)
                })
        val receiveScreenState by receiveViewModel.state.collectAsStateWithLifecycle()
        ReceiveScreen(
            navigateToP2PRoot = navigateToP2PRoot,
            screenState = receiveScreenState,
            errorsFlow = receiveViewModel.errors,
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
    navigateToP2PRoot: () -> Unit,
    screenState: ReceiveScreenState,
    errorsFlow: SharedFlow<String>,
    onEvent: (ReceiveScreenEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val isScreenLabelVisible =
            remember { MutableTransitionState(false) }.apply { targetState = true }
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            visibleState = isScreenLabelVisible,
            enter = slideInVertically(),
            exit = slideOutVertically()
        ) {
            Text(
                text = "You can receive banknotes on this screen",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .requiredHeight(50.dp)
            )
        }
        with(ReceiveScreenSubScreens) {
            AnimatedContent(
                modifier = Modifier,
                targetState = screenState,
                contentKey = { it.uiState },
                label = "ReceiveScreenAnimatedContent",
                transitionSpec = {
                    fadeIn(
                        tween(
                            1000,
                            0
                        )
                    ) togetherWith
                            fadeOut(
                                tween(
                                    1000,
                                    1000
                                )
                            )
                },
                contentAlignment = Alignment.Center,
            ) { state ->
                when (state.uiState) {
                    Initial -> InitialScreen(onClickStartAdvertising = { onEvent(ReceiveScreenEvent.SetAdvertising(true)) })
                    Advertising -> AdvertisingScreen(onClickStopAdvertising = { onEvent(ReceiveScreenEvent.SetAdvertising(false)) })
                    Loading -> InProgressScreen("Connecting")
                    is InTransaction -> TransactionBlock(
                        dataBuffer = screenState.transactionDataBuffer,
                        transactionStatus = state.uiState.status,
                        onEvent = onEvent
                    )
                    is OperationResult -> when (state.uiState.result) {
                        is Failure -> FailureScreen(lastException = screenState.transactionDataBuffer.lastException,
                            onClickDisconnect = { onEvent(ReceiveScreenEvent.Disconnect) })
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
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .requiredHeight(80.dp)
        ) { snackbarData ->
            CustomSnackbar(
                snackbarData,
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(40.dp)
                    .padding(
                        horizontal = 30.dp,
                        vertical = 0.dp
                    ),
                textColor = Color.White.copy(alpha = 0.8f),
                surfaceColor = Color.DarkGray.copy(alpha = 0.5f),
                borderColor = Color.Transparent
            )
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

    context(AnimatedContentScope)
    @Composable
    fun TransactionBlock(
        modifier: Modifier = Modifier,
        dataBuffer: TransactionDataBuffer,
        transactionStatus: ReceiverTransactionStatus,
        onEvent: (ReceiveScreenEvent) -> Unit
    ) {
        Column(modifier = modifier) {
            when (transactionStatus) {
                WAITING_FOR_OFFER -> ConnectedScreen(dataBuffer.otherUserInfo)
                OFFER_RECEIVED -> OfferReceivedScreen(amount = dataBuffer.amountRequest?.amount,
                    fromUser = dataBuffer.otherUserInfo,
                    onClickAccept = { onEvent(ReceiveScreenEvent.ReactToOffer(accept = true)) },
                    onClickReject = { onEvent(ReceiveScreenEvent.ReactToOffer(accept = false)) })

                // all the transaction info statuses are displayed here
                RECEIVING_BANKNOTES_LIST -> StatusInfoBlock(statusLabel = "Receiving banknotes...")
                BANKNOTES_LIST_RECEIVED -> StatusInfoBlock(
                    statusLabel = "Banknotes received",
                    infoText = "Quantity: ${dataBuffer.banknotesList?.list?.size}"
                )
                CREATING_SENDING_ACCEPTANCE_BLOCKS -> StatusInfoBlock(
                    statusLabel = "Creating acceptance blocks",
                    infoText = "Processing banknote # ${dataBuffer.currentlyProcessedBanknoteOrdinal + 1}"
                )
                WAITING_FOR_SIGNED_BLOCK -> StatusInfoBlock(
                    statusLabel = "Waiting for signed block",
                    infoText = "Processing banknote # ${dataBuffer.currentlyProcessedBanknoteOrdinal + 1}"
                )
                VERIFYING_RECEIVED_BLOCK -> StatusInfoBlock(
                    statusLabel = "Verifying received block",
                    infoText = "Processing banknote # ${dataBuffer.currentlyProcessedBanknoteOrdinal + 1}"
                )
                ALL_BANKNOTES_VERIFIED -> StatusInfoBlock(
                    statusLabel = "All banknotes verified!",
                    infoText = "Sending success confirmation..."
                )
                SAVING_BANKNOTES_TO_WALLET -> StatusInfoBlock(statusLabel = "Saving banknotes to the wallet...")
                BANKNOTES_SAVED -> StatusInfoBlock(statusLabel = "Banknotes are saved!")
                FINISHED_SUCCESSFULLY -> { /* TransactionBlock is not visible here,
                    ui state is automatically set to OperationResult Success in the viewmodel.*/
                }
                ERROR -> {/* TransactionBlock is not visible here,
                    ui state is automatically set to OperationResult Failure in the viewmodel.*/
                }
            }
            //todo can hide or make inactive on unsafe statuses.
            // Right now works only on safe statuses else triggers a warning snackbar
            ODCGradientActionButton(text = "Disconnect",
                gradientColors = GradientColors.ButtonNegativeActionColors,
                onClick = { onEvent(ReceiveScreenEvent.Disconnect) })
        }
    }


    @Composable
    fun InProgressScreen(label: String) {
        Column {
            Text(text = label)
            CircularProgressIndicator()
        }
    }

    context(AnimatedVisibilityScope)
    @Composable
    fun ConnectedScreen(
        otherUserInfo: UserInfo?
    ) {
        Column {
            UserInfoBlock(userInfo = otherUserInfo)
            Text(text = "Waiting for offer...") //todo add dots animation

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
                Text(text = fromUser?.userName ?: "null")
                Text(text = "Wallet ID:")
                Text(text = fromUser?.walletId ?: "null")
                Text(text = "Amount: $amount RUB")
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
    fun SuccessScreen(onClickFinish: () -> Unit) {
        Column {
            Text(text = "Transaction finished successfully")
            ODCGradientActionButton(
                text = "Finish",
                onClick = onClickFinish
            )
        }
    }

    @Composable
    fun FailureScreen(
        lastException: String?,
        onClickDisconnect: () -> Unit
    ) {
        Column {
            Text(text = "An error has occurred:")
            Text(text = lastException ?: "Unknown error")
            ODCGradientActionButton(
                text = "Disconnect",
                gradientColors = GradientColors.ButtonNegativeActionColors,
                onClick = onClickDisconnect
            )
        }
    }

}