package npo.kib.odc_demo.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import npo.kib.odc_demo.core.design_system.ui.ThemePreviews
import npo.kib.odc_demo.core.design_system.ui.theme.ODCAppTheme
import npo.kib.odc_demo.core.ui.R

@Preview(showBackground = true)
@Composable
fun UserPhotoSmall(
        modifier: Modifier = Modifier,
        shape: Shape = CircleShape,
        resId: Int = R.drawable.profile_pic_sample_square //todo pass image data to display, get image data from local storage
) {
    Image(
            modifier = modifier.aspectRatio(1f).clip(shape),
            painter = painterResource(id = resId),
            contentScale = ContentScale.Crop,
            contentDescription = "User Profile Picture"
    )
}

@Composable
@ThemePreviews
private fun UserPSPreview(){
    ODCAppTheme {
        UserPhotoSmall(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
        )
    }

}