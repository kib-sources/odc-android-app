package npo.kib.odc_demo.feature_app.presentation.p2p_screens.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.UserInfo
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.ODCGradientBackground
import npo.kib.odc_demo.ui.theme.ODCAppTheme

context(AnimatedVisibilityScope)
@Composable
fun UserInfoBlock(
    modifier: Modifier = Modifier,
    userInfo: UserInfo?
) {
    ODCGradientBackground(
        modifier = modifier,
        shape = RoundedCornerShape(10)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 10.dp,
                    horizontal = 0.dp
                )
                .animateFadeInOut(exitDelay = 200),
//            horizontalArrangement = Arrangement.Absolute.Left,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
                    .animateFadeInOut(enterDelay = 200),
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                Text(
                    text = "Connected to user:\n${userInfo?.userName}",
                    modifier = Modifier
                        .background(
                            Color.DarkGray.copy(0.2f),
                            RoundedCornerShape(20)
                        )
                        .padding(
                            vertical = 10.dp,
                            horizontal = 20.dp
                        )
                )
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
                    .animateFadeInOut(enterDelay = 200),
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                Text(
                    "User WID:\n${userInfo?.walletId}",
                    modifier = Modifier
                        .background(
                            Color.DarkGray.copy(0.2f),
                            RoundedCornerShape(20)
                        )
                        .padding(
                            vertical = 10.dp,
                            horizontal = 20.dp
                        )
                )
            }
        }
    }
}


@Composable
@Preview
private fun p() {
    ODCAppTheme {
        val visibleState = remember { MutableTransitionState(false) }.apply { targetState = true }
        AnimatedVisibility(visibleState = visibleState) {
            UserInfoBlock(
                modifier = Modifier.requiredHeightIn(
                    min = 50.dp,
                    max = 200.dp
                ),
                userInfo = UserInfo(
                    "Tim",
                    "12345"
                )
            )
        }
    }
}