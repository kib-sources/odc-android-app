package npo.kib.odc_demo.feature_app.presentation.common.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HistoryBlock(onHistoryClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Surface(
            shape = RoundedCornerShape(10),
            modifier = Modifier.fillMaxSize().border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(10)).clickable { onHistoryClick() }) {
        }
        Text("History is here! Click here!", modifier = Modifier.align(Alignment.Center))
    }

}