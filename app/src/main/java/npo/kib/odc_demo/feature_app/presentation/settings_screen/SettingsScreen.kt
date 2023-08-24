package npo.kib.odc_demo.feature_app.presentation.settings_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import npo.kib.odc_demo.ui.theme.ODCAppTheme
import npo.kib.odc_demo.ui.theme.Shapes

@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .requiredHeight(50.dp)
                    .clip(shape = Shapes.medium)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                               )
                    .clickable(onClick = onBackClick)
               ) {
                Text(
                    text = "Back", textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
            }
        }
    }

}


@Preview(showSystemUi = false)
@Composable
fun SettingsPreview() {
    ODCAppTheme {
        SettingsScreen(onBackClick = {})
    }
}