package npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen

import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import npo.kib.odc_demo.feature_app.presentation.common.LocalAppBluetoothPermissions
import npo.kib.odc_demo.feature_app.presentation.common.LocalReceiveViewModelNewFactory
import npo.kib.odc_demo.feature_app.presentation.common.permissions.getTextToShowGivenPermissions
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.MultiplePermissionsRequestBlock
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
//    LocalActivityResultRegistryOwner.current?.activityResultRegistry
    when (uiState) {
        ReceiveUiState.Initial -> InitialScreen(onClickStartAdvertising = {
            onEvent(
                ReceiveScreenEvent.SetAdvertising(true)
            )
        })

        ReceiveUiState.Advertising -> AdvertisingScreen(onClickStopAdvertising = {
            onEvent(ReceiveScreenEvent.SetAdvertising(false))
        })

        is ReceiveUiState.Paired -> PairedScreen()
        ReceiveUiState.OfferReceived -> OfferReceivedScreen(onEvent = onEvent)
        ReceiveUiState.Receiving -> ReceivingScreen()
        is ReceiveUiState.Result -> ResultScreen(uiState.result)
    }

}


@Composable
private fun InitialScreen(onClickStartAdvertising: () -> Unit) {
    Column {
        Text(text = "Initial screen")
        Button(onClick = onClickStartAdvertising) {
            Text(text = "Start advertising")
        }
    }
}

@Composable
private fun AdvertisingScreen(onClickStopAdvertising: () -> Unit) {
    Column {
        Text(text = "Advertising screen")
        Button(onClick = onClickStopAdvertising) {
            Text(text = "Stop advertising")
        }
    }
}

@Composable
fun PairedScreen() {

}

@Composable
private fun OfferReceivedScreen(onEvent: (ReceiveScreenEvent.ReactToOffer) -> Unit) {
    Column {
        Button(onClick = { onEvent(ReceiveScreenEvent.ReactToOffer(true)) }) {
            Text(text = "Accept")
        }
        Button(onClick = { onEvent(ReceiveScreenEvent.ReactToOffer(false)) }) {
            Text(text = "Reject")
        }
    }
}

@Composable
private fun ReceivingScreen() {
    Text(text = "Receiving banknotes...")
}

@Composable
private fun ResultScreen(result: ReceiveUiState.ResultType) {
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

@Preview
@Composable
fun ResultScreenPreviews() {
    ODCAppTheme {
        ResultScreen(result = ReceiveUiState.ResultType.Success)
        ResultScreen(result = ReceiveUiState.ResultType.Failure("Some error"))
    }
}