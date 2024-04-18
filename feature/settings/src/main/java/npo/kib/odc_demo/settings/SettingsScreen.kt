package npo.kib.odc_demo.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import npo.kib.odc_demo.core.design_system.components.ODCGradientButton
import npo.kib.odc_demo.core.design_system.components.ODCPlainButton
import npo.kib.odc_demo.core.design_system.components.TransparentHintTextField
import npo.kib.odc_demo.ui.GradientColors.ButtonNegativeActionColors
import npo.kib.odc_demo.ui.GradientColors.ButtonPositiveActionColors
import npo.kib.odc_demo.ui.theme.ODCAppTheme


@Composable
fun SettingsRoute(viewModel: SettingsViewModel = hiltViewModel(), onBackClick: () -> Unit) {
    val uiState by viewModel.settingsState.collectAsStateWithLifecycle()
    SettingsScreen(
        uiState, viewModel::saveChanges, onNameEntered = viewModel::onNameEntered, onBackClick
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsState, onClickSaveChanges: () -> Unit,
//    onPropertyChanged : ()
    onNameEntered: (String) -> Unit, onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Settings",
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 30.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
        Column(
            verticalArrangement = spacedBy(5.dp), modifier = Modifier
                .border(
                    width = Dp.Hairline,
                    shape = RoundedCornerShape(15),
                    color = MaterialTheme.colorScheme.onBackground
                )
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = "Current user name:", fontWeight = FontWeight.ExtraBold)
            Text(text = uiState.userName, fontStyle = FontStyle.Italic)
            Spacer(modifier = Modifier.height(5.dp))
            npo.kib.odc_demo.core.design_system.components.TransparentHintTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(Dp.Hairline, color = MaterialTheme.colorScheme.onBackground)
                    .padding(15.dp),
                hint = "Enter new name",
                onValueChange = onNameEntered,
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                singleLine = true
            )
        }
        Row(horizontalArrangement = spacedBy(10.dp)) {
            npo.kib.odc_demo.core.design_system.components.ODCGradientButton(
                Modifier.weight(1f),
                text = "Save changes",
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                gradientColors = if (uiState.isSaveButtonActive) ButtonPositiveActionColors else ButtonNegativeActionColors,
                enabled = uiState.isSaveButtonActive,
                onClick = onClickSaveChanges
            )
            npo.kib.odc_demo.core.design_system.components.ODCPlainButton(
                Modifier.weight(1f),
                text = "Go back",
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                color = Color.White,
                onClick = onBackClick
            )
        }
    }

}


@Preview(showSystemUi = false)
@Composable
fun SettingsPreview() {
    ODCAppTheme {
        SettingsScreen(SettingsState(), {}, {}, {})
    }
}