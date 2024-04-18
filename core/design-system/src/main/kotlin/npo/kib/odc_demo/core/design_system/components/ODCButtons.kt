package npo.kib.odc_demo.core.design_system.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import npo.kib.odc_demo.ui.GradientColors
import npo.kib.odc_demo.ui.ThemePreviews
import npo.kib.odc_demo.ui.theme.ODCAppTheme

@Composable
fun ODCPlainButton(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = odcButtonsTextStyle,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    shape: Shape = RoundedCornerShape(15),
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .background(
                color = color, shape = shape
            )
            .border(
                width = Dp.Hairline,
                shape = shape,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            .heightIn(min = ButtonDefaults.MinHeight),
        shape = shape,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        onClick = onClick
    ) {
        Text(text = text, style = textStyle)
    }

}

@ThemePreviews
@Composable
private fun ODCPlainActionButtonPreview() {
    ODCAppTheme {
        Column {
            ODCPlainButton(text = "Sample action") {}
        }
    }
}

@Composable
fun ODCGradientButton(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = odcButtonsTextStyle,
    gradientColors: GradientColors = GradientColors.ButtonPositiveActionColors,
    shape: Shape = RoundedCornerShape(15),
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        gradientColors.color1, gradientColors.color2
                    )
                ), shape = shape
            )
            .border(
                width = Dp.Hairline,
                shape = shape,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            .heightIn(min = ButtonDefaults.MinHeight),
        shape = shape,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        onClick = onClick
    ) {
        Text(text, style = textStyle)
    }

}

@ThemePreviews
@Composable
private fun ODCGradientActionButtonPreview() {
    ODCAppTheme {
        Column {
            ODCGradientButton(
                text = "Accept", gradientColors = GradientColors.ButtonPositiveActionColors
            ) {}
            ODCGradientButton(
                text = "Reject", gradientColors = GradientColors.ButtonNegativeActionColors
            ) {}
        }
    }
}

val odcButtonsTextStyle: TextStyle
    @Composable get() = TextStyle(
        color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center
    )