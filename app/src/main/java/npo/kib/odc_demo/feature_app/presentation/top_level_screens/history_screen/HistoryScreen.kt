package npo.kib.odc_demo.feature_app.presentation.top_level_screens.history_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

//@Composable
//fun HistoryScreenRoute() {
//
//}

@Composable
fun HistoryScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(color = Color.LightGray, modifier = Modifier.fillMaxSize()) {
        }
        Text(text = "HISTORY_SCREEN! ", modifier = Modifier.align(Alignment.Center))
    }
}