package npo.kib.odc_demo.feature_app.presentation.common.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import npo.kib.odc_demo.ui.GradientColors
import npo.kib.odc_demo.ui.ThemePreviews
import npo.kib.odc_demo.ui.theme.ODCAppTheme

@Composable
fun ODCPlainActionButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    ODCPlainBackground(modifier = modifier) {
        Button(onClick = onClick) {
            Text(text = text)
        }
    }
}

@ThemePreviews
@Composable
private fun ODCPlainActionButtonPreview() {
    ODCAppTheme {
        ODCPlainActionButton(text = "Sample action"){}
    }
}

@Composable
fun ODCGradientActionButton(
    modifier: Modifier = Modifier,
    text: String,
    gradientColors: GradientColors = GradientColors.ButtonPositiveActionColors,
    onClick: () -> Unit
) {
    ODCGradientBackground(modifier = modifier, gradientColors = gradientColors) {
        Button(onClick = onClick) {
            Text(text = text)
        }
    }
}

@ThemePreviews
@Composable
private fun ODCGradientActionButtonPreview() {
    ODCAppTheme {
        Column {
        ODCGradientActionButton(text = "Accept", gradientColors = GradientColors.ButtonPositiveActionColors){}
        ODCGradientActionButton(text = "Reject", gradientColors = GradientColors.ButtonNegativeActionColors){}
        }
    }
}