@file:OptIn(ExperimentalAnimationApi::class)

package npo.kib.odc_demo.p2p.receive_screen

import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.data.permissions.PermissionProvider.LocalAppBluetoothPermissions
import npo.kib.odc_demo.feature_app.data.permissions.getTextToShowGivenPermissions
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.UserInfo
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionDataBuffer
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionStatus.ReceiverTransactionStatus
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionStatus.ReceiverTransactionStatus.*
import npo.kib.odc_demo.core.design_system.components.CustomSnackbar
import npo.kib.odc_demo.core.design_system.components.MultiplePermissionsRequestBlock
import npo.kib.odc_demo.core.design_system.components.ODCGradientButton
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.common.components.StatusInfoBlock
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.common.components.UserInfoBlock
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.common.components.animateFadeVerticalSlideInOut
import npo.kib.odc_demo.p2p.receive_screen.ReceiveScreenEvent.ReactToOffer
import npo.kib.odc_demo.p2p.receive_screen.ReceiveScreenEvent.SetAdvertising
import npo.kib.odc_demo.p2p.receive_screen.ReceiveUiState.*
import npo.kib.odc_demo.p2p.receive_screen.ReceiveUiState.OperationResult.ResultType.Failure
import npo.kib.odc_demo.p2p.receive_screen.ReceiveUiState.OperationResult.ResultType.Success
import npo.kib.odc_demo.p2p.receive_screen.ReceiveViewModel.Companion.ReceiveViewModelFactory
import npo.kib.odc_demo.ui.GradientColors.ButtonNegativeActionColors
import npo.kib.odc_demo.ui.GradientColors.ButtonPositiveActionColors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReceiveRoute(
    navigateToP2PRoot: () -> Unit, navBackStackEntry: NavBackStackEntry
) {
    val multiplePermissionsState =
        rememberMultiplePermissionsState(permissions = LocalAppBluetoothPermissions.current)
    if (multiplePermissionsState.allPermissionsGranted) {
//      If all the required bluetooth permissions are granted, the viewmodel is created and ReceiveScreen() is launched
        val registry = LocalActivityResultRegistryOwner.current!!.activityResultRegistry
        val receiveViewModel: ReceiveViewModel =
            hiltViewModel(viewModelStoreOwner = navBackStackEntry,
                creationCallback = { factory: ReceiveViewModelFactory ->
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
        LaunchedEffect(key1 = multiplePermissionsState.allPermissionsGranted) {
            launch { navBackStackEntry.viewModelStore.clear() }
        }
        npo.kib.odc_demo.core.design_system.components.MultiplePermissionsRequestBlock(
            permissionsRequestText = getTextToShowGivenPermissions(
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
                text = "You can receive banknotes on this screen",
                modifier = Modifier.requiredHeight(50.dp),
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.Underline
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        val showUserInfoBlock by remember(screenState.uiState) {
            mutableStateOf(screenState.uiState is InTransaction)
        }
        if (showUserInfoBlock) AnimatedVisibility(
            visible = true, enter = EnterTransition.None, exit = ExitTransition.None
        ) {
            UserInfoBlock(
                modifier = Modifier.animateFadeVerticalSlideInOut(),
                userInfo = screenState.transactionDataBuffer.otherUserInfo
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        with(ReceiveScreenSubScreens) {
            AnimatedContent(
                modifier = Modifier.fillMaxWidth(),
                targetState = screenState,
                contentKey = { it.uiState },
                label = "ReceiveScreenAnimatedContent",
                transitionSpec = {
                    fadeIn(tween(1000)) togetherWith fadeOut(tween(1000))
                },
                contentAlignment = Alignment.Center,
            ) { state ->

                when (val uiState = state.uiState) {
                    Initial -> InitialScreen(onClickStartAdvertising = {
                        onEvent(SetAdvertising(true))
                    })

                    Advertising -> AdvertisingScreen(onClickStopAdvertising = {
                        onEvent(SetAdvertising(false))
                    })

                    Loading -> InProgressScreen("Connecting")

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
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .requiredHeightIn(min = 80.dp, max = 250.dp)
        ) { snackbarData ->
            npo.kib.odc_demo.core.design_system.components.CustomSnackbar(
                snackbarData,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp),
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
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(text = "Initial screen")
            Spacer(modifier = Modifier.height(15.dp))
            npo.kib.odc_demo.core.design_system.components.ODCGradientButton(
                text = "Start advertising",
                onClick = onClickStartAdvertising
            )
        }
    }

    @Composable
    fun AdvertisingScreen(onClickStopAdvertising: () -> Unit) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Advertising screen")
            npo.kib.odc_demo.core.design_system.components.ODCGradientButton(
                text = "Stop advertising",
                onClick = onClickStopAdvertising,
                gradientColors = ButtonNegativeActionColors
            )
        }
    }

    context(AnimatedContentScope)
    @Composable
    fun TransactionBlock(
        dataBuffer: TransactionDataBuffer,
        transactionStatus: ReceiverTransactionStatus,
        onEvent: (ReceiveScreenEvent) -> Unit
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (transactionStatus) {
                WAITING_FOR_OFFER -> ConnectedScreen()
                OFFER_RECEIVED -> OfferReceivedScreen(amount = dataBuffer.amountRequest?.amount,
                    fromUser = dataBuffer.otherUserInfo,
                    onClickAccept = { onEvent(ReactToOffer(accept = true)) },
                    onClickReject = { onEvent(ReactToOffer(accept = false)) })

                RECEIVING_BANKNOTES_LIST -> StatusInfoBlock(statusLabel = "Receiving banknotes...")
                BANKNOTES_LIST_RECEIVED -> StatusInfoBlock(
                    statusLabel = "Banknotes received",
                    infoText = "Quantity: ${dataBuffer.banknotesList?.list?.size}"
                )

                CREATING_SENDING_ACCEPTANCE_BLOCKS -> StatusInfoBlock(
                    statusLabel = "Creating acceptance blocks",
                    infoText = "Processing banknote # ${dataBuffer.currentlyProcessedBanknoteIndex + 1}"
                )

                WAITING_FOR_SIGNED_BLOCK -> StatusInfoBlock(
                    statusLabel = "Waiting for signed block",
                    infoText = "Processing banknote # ${dataBuffer.currentlyProcessedBanknoteIndex + 1}"
                )

                VERIFYING_RECEIVED_BLOCK -> StatusInfoBlock(
                    statusLabel = "Verifying received block",
                    infoText = "Processing banknote # ${dataBuffer.currentlyProcessedBanknoteIndex + 1}"
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
            //fixme currently crashes the app
            //fixme now is hidden behind bottom navigation bar
//            ODCGradientActionButton(text = "Disconnect",
//                gradientColors = GradientColors.ButtonNegativeActionColors,
//                onClick = { onEvent(ReceiveScreenEvent.Disconnect) })
        }
    }


    context(AnimatedVisibilityScope)
    @Composable
    fun InProgressScreen(label: String) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StatusInfoBlock(statusLabel = label)
        }
    }

    context(AnimatedVisibilityScope)
    @Composable
    fun ConnectedScreen() {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StatusInfoBlock(statusLabel = "Waiting for offer...") //todo add dots animation
        }
    }

    context(AnimatedVisibilityScope)
    @Composable
    fun OfferReceivedScreen(
        amount: Int?, fromUser: UserInfo?, onClickAccept: () -> Unit, onClickReject: () -> Unit
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StatusInfoBlock(
                statusLabel = "Received offer from", infoText = fromUser?.userName ?: "null"
            )
            StatusInfoBlock(
                statusLabel = "Amount:", infoText = "$amount RUB"
            )
            Spacer(modifier = Modifier.height(5.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                npo.kib.odc_demo.core.design_system.components.ODCGradientButton(
                    Modifier.weight(1f),
                    text = "Accept",
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                    gradientColors = ButtonPositiveActionColors,
                    onClick = onClickAccept
                )
                npo.kib.odc_demo.core.design_system.components.ODCGradientButton(
                    Modifier.weight(1f),
                    text = "Reject",
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                    gradientColors = ButtonNegativeActionColors,
                    onClick = onClickReject
                )
            }
        }
    }

    @Composable
    fun SuccessScreen(onClickFinish: () -> Unit) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Transaction finished successfully")
            npo.kib.odc_demo.core.design_system.components.ODCGradientButton(
                text = "Finish", onClick = onClickFinish
            )
        }
    }

    @Composable
    fun FailureScreen(
        failureMessage: String, onClickAbort: () -> Unit
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "An error has occurred:")
            Text(text = failureMessage)
            npo.kib.odc_demo.core.design_system.components.ODCGradientButton(
                text = "Abort transaction",
                gradientColors = ButtonNegativeActionColors,
                onClick = onClickAbort
            )
        }
    }

}