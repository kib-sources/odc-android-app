package npo.kib.odc_demo.feature_app.presentation.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import npo.kib.odc_demo.ui.GradientColors
import npo.kib.odc_demo.ui.ThemePreviews
import npo.kib.odc_demo.ui.theme.ODCAppTheme


@Composable
fun ODCPlainBackground(
    modifier: Modifier = Modifier, content: @Composable () -> Unit
) {
    // Can create later an @Immutable class to model background color and tonal elevation values for the app
    //accessed with composition local prop LocalBackgroundTheme created at the same level ( staticCompositionLocalOf{} )
//    val color = LocalBackgroundTheme.current.color
//    val tonalElevation = LocalBackgroundTheme.current.tonalElevation
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        CompositionLocalProvider(LocalAbsoluteTonalElevation provides 0.dp) {
            content()
        }
    }
}


@Composable
fun ODCGradientBackground(
    modifier: Modifier = Modifier,
    gradientColors: GradientColors = GradientColors.ColorSet1,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier, color = if (gradientColors.container == Color.Unspecified) {
            Color.Transparent
        } else {
            gradientColors.container
        }
    ) {
        Box(
            //todo replace with Modifier.drawWithCache {}
            modifier = Modifier.background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        gradientColors.color1, gradientColors.color2
                    ), start = Offset(0f, 0f), end = Offset.Infinite
                )
            )
        ) {
            content()
        }
    }

}


@ThemePreviews
@Composable
private fun ODCPlainBackgroundPreview() {
    ODCAppTheme {
        ODCPlainBackground(Modifier.size(200.dp), content = {})
    }
}


@ThemePreviews
@Composable
private fun ODCGradientBackgroundPreview() {
    ODCAppTheme {
        ODCGradientBackground(Modifier.size(200.dp), content = {})
    }
}