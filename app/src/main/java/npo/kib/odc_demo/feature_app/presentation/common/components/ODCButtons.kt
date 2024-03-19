package npo.kib.odc_demo.feature_app.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import npo.kib.odc_demo.ui.GradientColors
import npo.kib.odc_demo.ui.ThemePreviews
import npo.kib.odc_demo.ui.theme.ODCAppTheme

@Composable
fun ODCPlainActionButton(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    shape: Shape = RoundedCornerShape(50),
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .background(
                color = color,
                shape = shape
            )
            .border(
                width = Dp.Hairline,
                shape = shape,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            .height(ButtonDefaults.MinHeight)
            .aspectRatio(4f),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Text(text = text)
    }

}

@ThemePreviews
@Composable
private fun ODCPlainActionButtonPreview() {
    ODCAppTheme {
        Column {
            ODCPlainActionButton(text = "Sample action") {}
        }
    }
}

@Composable
fun ODCGradientActionButton(
    modifier: Modifier = Modifier,
    text: String,
    gradientColors: GradientColors = GradientColors.ButtonPositiveActionColors,
    shape: Shape = RoundedCornerShape(50),
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        gradientColors.color1,
                        gradientColors.color2
                    )
                ),
                shape = shape
            )
            .border(
                width = Dp.Hairline,
                shape = shape,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            /*.height(ButtonDefaults.MinHeight)
            .aspectRatio(4f)*/,
        enabled = enabled,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Text(text)
    }

}

@ThemePreviews
@Composable
private fun ODCGradientActionButtonPreview() {
    ODCAppTheme {
        Column {
            ODCGradientActionButton(
                text = "Accept",
                gradientColors = GradientColors.ButtonPositiveActionColors
            ) {}
            ODCGradientActionButton(
                text = "Reject",
                gradientColors = GradientColors.ButtonNegativeActionColors
            ) {}
        }
    }
}