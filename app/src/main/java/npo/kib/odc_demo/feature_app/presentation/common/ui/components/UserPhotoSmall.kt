package npo.kib.odc_demo.feature_app.presentation.common.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import npo.kib.odc_demo.R

@Preview(showBackground = true)
@Composable
fun UserPhotoSmall(
    modifier: Modifier = Modifier,
    /*, size: Dp = 75.dp*/
    resId: Int = R.drawable.profile_pic_sample_square
) {
    Image(
        modifier = modifier
//            .size(size)
//            .shadow(20.dp, CircleShape)
            .clip(CircleShape),
        painter = painterResource(id = resId),
        contentDescription = "User Profile Picture"
    )

}