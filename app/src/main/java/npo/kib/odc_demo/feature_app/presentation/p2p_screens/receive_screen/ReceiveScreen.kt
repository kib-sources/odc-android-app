package npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen

import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.CustomBluetoothDevice
import npo.kib.odc_demo.feature_app.presentation.common.LocalReceiveViewModelNewFactory
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.MultiplePermissionsRequestBlock
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.common.components.DeviceItem
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen.ReceiveScreenSubScreens.ResultScreen
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

//    val viewModel = hiltViewModel<ReceiveViewModelNew>(viewModelStoreOwner = it) <- without assisted injection.
//    Using CompositionLocalProvider to provide ReceiveViewModelNew.Factory and then
//    creating a viewmodel scoped to the NavBackStackEntry with Hilt 's Assisted injection
//    providing activityResultRegistry to be able to use ActivityResult API in the viewmodel.
        val viewModel = viewModel<ReceiveViewModelNew>(
            viewModelStoreOwner = navBackStackEntry,
            factory = ReceiveViewModelNew.provideReceiveViewModelNewFactory(
                LocalReceiveViewModelNewFactory.current!!,
                registry = LocalActivityResultRegistryOwner.current!!.activityResultRegistry
            )
        )
        val receiveUiState by viewModel.uiState.collectAsStateWithLifecycle()
        ReceiveScreen(uiState = receiveUiState, onEvent = viewModel::onEvent)
    } else {
        MultiplePermissionsRequestBlock(
            permissionsRequestText = getTextToShowGivenPermissions(
                multiplePermissionsState.revokedPermissions,
                multiplePermissionsState.shouldShowRationale,
            ),
            onRequestPermissionsClick = { multiplePermissionsState.launchMultiplePermissionRequest() })
    }

}


@Composable
internal fun ReceiveScreen(
    uiState: ReceiveUiState, onEvent: (ReceiveScreenEvent) -> Unit
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

            ReceiveUiState.WaitingForConnection -> AdvertisingScreen(onClickStopAdvertising = {
                onEvent(ReceiveScreenEvent.SetAdvertising(false))
            })

            is ReceiveUiState.Connected -> ConnectedScreen(CustomBluetoothDevice("Test","Test"))
            is ReceiveUiState.OfferReceived -> {}/*OfferReceivedScreen(uiState., onEvent = onEvent)*/
            ReceiveUiState.Receiving -> ReceivingScreen()
            is ReceiveUiState.Result -> ResultScreen(uiState.result)
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
    fun ConnectedScreen(bluetoothDevice: CustomBluetoothDevice) {
        Column {
            Text(text = "Connected to device:")
            Spacer(modifier = Modifier.height(5.dp))
            DeviceItem(
                name = bluetoothDevice.name ?: "No name",
                address = bluetoothDevice.address,
                onItemClick = {})
        }
    }

    @Composable
    fun OfferReceivedScreen(
        name: String,
        info: String,
        onEvent: (ReceiveScreenEvent.ReactToOffer) -> Unit
    ) {
        Surface {
            Column {
                Text(text = "Offer received from: ")
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = name)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "Info:")
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = info)
                Row {
                    Button(onClick = { onEvent(ReceiveScreenEvent.ReactToOffer(true)) }) {
                        Text(text = "Accept")
                    }
                    Button(onClick = { onEvent(ReceiveScreenEvent.ReactToOffer(false)) }) {
                        Text(text = "Reject")
                    }
                }
            }
        }
    }

    @Composable
    fun ReceivingScreen() {
        Text(text = "Receiving banknotes...")
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