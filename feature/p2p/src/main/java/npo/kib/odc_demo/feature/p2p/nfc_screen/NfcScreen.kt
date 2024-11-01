package npo.kib.odc_demo.feature.p2p.nfc_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import npo.kib.odc_demo.core.design_system.components.ODCGradientButton

@Composable
fun NfcRoute(
    navigateToP2PRoot: () -> Unit, navBackStackEntry: NavBackStackEntry
) {

//    val registry = LocalActivityResultRegistryOwner.current!!.activityResultRegistry
    val viewModel: NfcViewModel =
        hiltViewModel(
            navBackStackEntry
        )
    NfcScreen { viewModel.onEvent(it) }
}

@Composable
private fun NfcScreen(
    onEvent: (NfcScreenEvent) -> Unit
){
    Column {
        ODCGradientButton(
            text = "Start advertising",
            onClick = {
                onEvent(NfcScreenEvent.StartReceiving)
            }
        )
    }
}