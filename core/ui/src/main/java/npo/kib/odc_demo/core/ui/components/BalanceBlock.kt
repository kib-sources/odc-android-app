package npo.kib.odc_demo.core.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import npo.kib.odc_demo.core.design_system.ui.theme.CustomColors
import npo.kib.odc_demo.core.design_system.components.rotatingOnCondition
import npo.kib.odc_demo.core.design_system.ui.theme.ODCAppTheme
import npo.kib.odc_demo.core.model.user.AppUser
import npo.kib.odc_demo.core.ui.icon.ODCIcon

@Composable
fun BalanceBlock(
    modifier: Modifier = Modifier,
    balance: Int = 0,
    appUser: AppUser = AppUser(),
    roundedCornerSize: Dp = 15.dp,
    refreshBalanceAndUserInfo: () -> Unit = {},
    onWalletDetailsClick: () -> Unit,
    isUpdatingBalanceAndInfo: Boolean = false,
    textColor: Color = White,
    surfaceColor: Color = CustomColors.Confirm_Success
) {
    Surface(
        modifier = modifier
            .requiredHeight(67.dp)
            .aspectRatio(4.6f)
            .clip(RoundedCornerShape(roundedCornerSize)), color = surfaceColor
    ) {
        Row {
            Row(
                Modifier
                    .fillMaxHeight()
                    .weight(3f)
            ) {
                Column(
                    Modifier
                        .fillMaxHeight()
                        .weight(3f)
                        .padding(
                            start = roundedCornerSize + 15.dp, top = 10.dp, bottom = 10.dp
                        ).clickable { onWalletDetailsClick() }
                ) {
                    Text(
                        modifier = Modifier.weight(0.5f),
                        text = "Your Balance",
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        fontSize = 13.sp,
                        color = textColor
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "$balance RUB",
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        fontSize = 24.sp,
                        color = textColor
                    )
                }
                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(), verticalArrangement = Arrangement.Center
                ) {
                    RefreshIcon(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .align(Alignment.CenterHorizontally),
                        isRefreshing = isUpdatingBalanceAndInfo,
                        onClickRefresh = refreshBalanceAndUserInfo
                    )
                }
            }
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(
                        end = roundedCornerSize, top = 3.dp, bottom = 3.dp
                    )


            ) {
                UserPhotoSmall(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .aspectRatio(1f)
                        .border(
                            Dp.Hairline,
                            Black,
                            CircleShape
                        )
                        .weight(1f)
                )
                Text(
                    modifier = Modifier
                        .weight(0.4f)
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 1.dp),
                    text = appUser.userName,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    fontSize = 10.sp,
                    color = textColor
                )
            }
        }
    }
}



@Composable
fun RefreshIcon(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean = false,
    onClickRefresh: () -> Unit,
) {
    IconButton(
        modifier = modifier, enabled = !isRefreshing, onClick = onClickRefresh
    ) {
        Icon(modifier = Modifier
            .rotatingOnCondition(
                isRotating = isRefreshing,
                isClockwise = false,
                duration = 1500,
                easing = FastOutSlowInEasing
            )
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            DarkGray, Transparent
                        ), radius = size.maxDimension * 1f
                    ),
                    radius = size.maxDimension * 3f,
                    blendMode = BlendMode.ColorDodge,
                )
            }
            .scale(
                -1.1f, 1.1f
            ),
            painter = painterResource(id = ODCIcon.RefreshIcon.resId),
            contentDescription = "Refresh clickable icon",
            tint = White)
    }

}

@Preview
@Composable
private fun PrevBalanceBlock() {
    ODCAppTheme {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            var isUpdating by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()
            BalanceBlock(
                isUpdatingBalanceAndInfo = isUpdating,
                refreshBalanceAndUserInfo = {
                    scope.launch {
                        isUpdating = true
                        delay(4321)
                        isUpdating = false
                    }
                }, onWalletDetailsClick = {})
            BalanceBlock(isUpdatingBalanceAndInfo = true, onWalletDetailsClick = {})
        }
    }
}