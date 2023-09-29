package npo.kib.odc_demo.feature_app.presentation.common.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.unit.dp
import npo.kib.odc_demo.ui.ThemePreviews
import npo.kib.odc_demo.ui.theme.ODCAppTheme


@Composable
fun ODCPlainBackground(modifier: Modifier = Modifier,
                       content: @Composable () -> Unit) {
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
//    create an @Immutable model class for Gradient color values later with composition local prop for it
//    gradientColors: GradientColor,
    content: @Composable () -> Unit
) {
    Surface() {
        Box(
            modifier = Modifier.fillMaxSize().drawWithCache {
                onDrawBehind {
                }
            }
        ){
            content()
        }
    }

}


@ThemePreviews
@Composable
fun BackgroundDefault() {
    ODCAppTheme {
        ODCPlainBackground(Modifier.size(100.dp), content = {})
    }
}


@ThemePreviews
@Composable
fun GradientBackgroundDefault() {
    ODCAppTheme {
        ODCGradientBackground(Modifier.size(100.dp), content = {})
    }
}