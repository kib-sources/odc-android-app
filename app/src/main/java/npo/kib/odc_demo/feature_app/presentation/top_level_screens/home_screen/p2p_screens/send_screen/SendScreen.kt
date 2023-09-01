package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.p2p_screens.send_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun SendScreen(onClick : () -> Unit){
    Box(modifier = Modifier.fillMaxSize()){
        Button(onClick = onClick, modifier = Modifier.aspectRatio(1f).size(100.dp).align(Alignment.Center), shape = RectangleShape) {

        }
    }
}