package npo.kib.odc_demo.feature_app.presentation.top_level_screens.settings_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import npo.kib.odc_demo.ui.theme.ODCAppTheme
import npo.kib.odc_demo.ui.theme.Shapes

//todo create @Composable SettingsRoute() like in NIA (?)
@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            ).padding(20.dp), contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = "Settings",
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = "Back", textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clickable { onBackClick() },
        )
    }

}


@Preview(showSystemUi = false)
@Composable
fun SettingsPreview() {
    ODCAppTheme {
        SettingsScreen(onBackClick = {})
    }
}