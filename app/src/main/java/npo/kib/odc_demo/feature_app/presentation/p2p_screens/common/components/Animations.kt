package npo.kib.odc_demo.feature_app.presentation.p2p_screens.common.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import npo.kib.odc_demo.ui.ThemePreviews
import npo.kib.odc_demo.ui.theme.ODCAppTheme

@Composable
fun AdvertisingAnimation() {

}

@Composable
fun SearchingAnimation() {

}

@Composable
fun ProgressBar() {

}

@Composable
fun LoadingAnimation() {
    CircularProgressIndicator()
}


@ThemePreviews
@Composable
private fun LoadingAnimationPreview() {
    ODCAppTheme {
        LoadingAnimation()
    }
}