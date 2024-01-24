package npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen

import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import npo.kib.odc_demo.feature_app.data.permissions.PermissionProvider.LocalAppBluetoothPermissions
import npo.kib.odc_demo.feature_app.data.permissions.getTextToShowGivenPermissions
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.AmountRequest
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.UserInfo
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.CustomBluetoothDevice
import npo.kib.odc_demo.feature_app.presentation.common.LocalReceiveViewModelFactory
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.MultiplePermissionsRequestBlock
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.ODCGradientActionButton
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.common.components.DeviceItem
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveScreenSubScreens.ResultScreen
import npo.kib.odc_demo.ui.GradientColors
import npo.kib.odc_demo.ui.theme.ODCAppTheme


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReceiveRoute(
    navBackStackEntry: NavBackStackEntry
) {
    val multiplePermissionsState = rememberMultiplePermissionsState(permissions = LocalAppBluetoothPermissions.current)
    if (multiplePermissionsState.allPermissionsGranted) {
//    If all the required bluetooth permissions are granted, the viewmodel is created and ReceiveScreen() is launched

//    val viewModel = hiltViewModel<ReceiveViewModelNew>(viewModelStoreOwner = it) <- without assisted injection.
//    Using CompositionLocalProvider to provide ReceiveViewModelNew.Factory and then
//    creating a viewmodel scoped to the NavBackStackEntry with Hilt 's Assisted injection
//    providing activityResultRegistry to be able to use ActivityResult API in the viewmodel.
        val viewModel = viewModel<ReceiveViewModel>(
            viewModelStoreOwner = navBackStackEntry, factory = ReceiveViewModel.provideReceiveViewModelNewFactory(
                LocalReceiveViewModelFactory.current!!,
                registry = LocalActivityResultRegistryOwner.current!!.activityResultRegistry
            )
        )
        //todo change type from ReceiveUiState to ReceiveScreenState
        val receiveUiState by viewModel.uiState.collectAsStateWithLifecycle()
        val receiveScreenState by viewModel.uiState.collectAsStateWithLifecycle()
        ReceiveScreen(uiState = receiveUiState, onEvent = viewModel::onEvent)
    }
    else {
        MultiplePermissionsRequestBlock(permissionsRequestText = getTextToShowGivenPermissions(
            multiplePermissionsState.revokedPermissions,
            multiplePermissionsState.shouldShowRationale,
        ), onRequestPermissionsClick = { multiplePermissionsState.launchMultiplePermissionRequest() })
    }

}


@Composable
private fun ReceiveScreen(
    uiState: ReceiveUiState,
    onEvent: (ReceiveScreenEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val isScreenLabelVisible by remember { mutableStateOf(true) }
        AnimatedVisibility(
            visible = isScreenLabelVisible,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Text(text = "Receive banknotes", modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        with(ReceiveScreenSubScreens) {
            when (uiState) {
                ReceiveUiState.Initial -> InitialScreen(onClickStartAdvertising = {
                    onEvent(
                        ReceiveScreenEvent.SetAdvertising(true)
                    )
                })

                ReceiveUiState.Advertising -> AdvertisingScreen(onClickStopAdvertising = {
                    onEvent(ReceiveScreenEvent.SetAdvertising(false))
                })
                //todo on this screen could already have received the other UserInfo automatically
                // to react to connection request
                is ReceiveUiState.ConnectionRequestReceived -> ConnectionRequestedScreen(fromDevice = /*todo change uiState to ReceiveScreenState*/ uiState.fromDevice,
                    onClickAccept = { onEvent(ReceiveScreenEvent.ReactToConnection(accept = true)) },
                    onClickReject = { onEvent(ReceiveScreenEvent.ReactToConnection(accept = false)) })
                //todo on this screen could already have received the other UserInfo automatically
                is ReceiveUiState.Connected -> ConnectedScreen(CustomBluetoothDevice("Test", "Test"))
                is ReceiveUiState.OfferReceived -> OfferReceivedScreen(/*todo change to SendScreenState.transactionDataBuffer.amountRequest later*/
                    amountRequest = AmountRequest(
                        1000, ""
                    ),/*todo change to SendScreenState.transactionDataBuffer.otherUserInfo later*/
                    fromUser = UserInfo(userName = "Other user", walletId = ""),
                    onClickAccept = { onEvent(ReceiveScreenEvent.ReactToOffer(accept = true)) },
                    onClickReject = { onEvent(ReceiveScreenEvent.ReactToOffer(accept = false)) })

                ReceiveUiState.ReceivingAllBanknotes -> ReceivingAllBanknotesScreen()
                ReceiveUiState.ProcessingBanknote -> ProcessingBanknoteScreen(banknoteId = -1)

                is ReceiveUiState.Result -> when (uiState.result) {
                    is ReceiveUiState.ResultType.Failure -> FailureScreen(onClickRetry = {})
                    ReceiveUiState.ResultType.Success -> SuccessScreen(onClickFinish = {})
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

    @Composable
    fun ConnectionRequestedScreen(
        fromDevice: CustomBluetoothDevice,
        onClickAccept: () -> Unit,
        onClickReject: () -> Unit
    ) {
        Column {
            Text(text = "Connection requested from device:")
            DeviceItem(name = fromDevice.name, address = fromDevice.address, onItemClick = {})
            ODCGradientActionButton(
                text = "Accept connection",
                gradientColors = GradientColors.ButtonPositiveActionColors,
                onClick = onClickAccept
            )
            ODCGradientActionButton(
                text = "Reject connection",
                gradientColors = GradientColors.ButtonNegativeActionColors,
                onClick = onClickReject
            )
        }
    }

    @Composable
    fun ConnectedScreen(bluetoothDevice: CustomBluetoothDevice) {
        Column {
            Text(text = "Connected to device:")
            Spacer(modifier = Modifier.height(5.dp))
            DeviceItem(name = bluetoothDevice.name ?: "No name", address = bluetoothDevice.address, onItemClick = {})
        }
    }

    @Composable
    fun OfferReceivedScreen(
        fromUser: UserInfo,
        amountRequest: AmountRequest,
        onClickAccept: () -> Unit,
        onClickReject: () -> Unit
    ) {
        Surface {
            Column {
                Text(text = "Offer received from: ")
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = fromUser.userName)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "Wallet ID:")
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = fromUser.walletId)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "Amount:")
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "${amountRequest.amount} RUB")
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
    fun ResultScreen(result: ReceiveUiState.ResultType) {
        when (result) {
            ReceiveUiState.ResultType.Success -> Column {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Receiving success!",
                    color = Color.Green
                )
            }

            is ReceiveUiState.ResultType.Failure -> Column {
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
            ODCGradientActionButton(text = "Finish", gradientColors = GradientColors.ColorSet2, onClick = onClickFinish)
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
            ResultScreen(result = ReceiveUiState.ResultType.Success)
            ResultScreen(result = ReceiveUiState.ResultType.Failure("Some error"))
        }
    }
}