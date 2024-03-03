package npo.kib.odc_demo.feature_app.presentation.p2p_screens.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.UserInfo
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.ODCPlainBackground
import npo.kib.odc_demo.ui.theme.ODCAppTheme

@Composable
fun UserInfoBlock(modifier: Modifier = Modifier, userInfo: UserInfo?) {
    ODCPlainBackground(modifier = modifier, shape = RoundedCornerShape(10)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                Text(text = "Connected to user:")
                Text("${userInfo?.userName}")
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                Text("User WID:")
                Text("${userInfo?.walletId}")
            }
        }
    }
}


@Composable
@Preview
private fun p() {
    ODCAppTheme {
        UserInfoBlock(
            modifier = Modifier.requiredHeightIn(min = 50.dp, max = 200.dp),
            userInfo = UserInfo("Tim", "12345")
        )
    }
}