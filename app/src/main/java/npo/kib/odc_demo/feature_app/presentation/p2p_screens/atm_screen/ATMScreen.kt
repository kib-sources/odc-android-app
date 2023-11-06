package npo.kib.odc_demo.feature_app.presentation.p2p_screens.atm_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ATMRoute(viewModel : ATMViewModelNew) {
    
}
@Composable
fun ATMScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(color = Color.Blue, modifier = Modifier.fillMaxSize()) {
        }
        Text(text = "ATM_SCREEN! ", modifier = Modifier.align(Alignment.Center))
    }
}

