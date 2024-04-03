package npo.kib.odc_demo.feature_app.presentation.top_level_screens.history_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import npo.kib.odc_demo.feature_app.presentation.common.components.ODCGradientBackground
import npo.kib.odc_demo.ui.GradientColors

//@Composable
//fun HistoryScreenRoute() {
//
//}

@Composable
fun HistoryScreen() {
    ODCGradientBackground(Modifier.padding(horizontal = 5.dp, vertical = 10.dp),gradientColors = GradientColors.ColorSet2) {
        Box(Modifier.fillMaxSize()) {
            Text(text = "Transaction history! ", modifier = Modifier.align(Alignment.Center))
        }
    }
}