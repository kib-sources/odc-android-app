package npo.kib.odc_demo.feature_app.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import npo.kib.odc_demo.R
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.UserInfo
import npo.kib.odc_demo.ui.ODCIcons
import npo.kib.odc_demo.ui.theme.CustomColors

@Preview(showBackground = true)
@Composable
fun BalanceBlock(
    modifier: Modifier = Modifier,
    balance: Int = 0,
    roundedCornerSize: Dp = 15.dp,
    userPhotoSmallComposable: @Composable () -> Unit = { UserPhotoSmall() },
    refreshAmountInWallet : () -> Unit = {},
    textColor: Color = Color.White,
    surfaceColor: Color = CustomColors.Confirm_Success
) {
    Surface(
        modifier = modifier
            .requiredHeight(67.dp)
            .aspectRatio(4.6f)
            .clip(RoundedCornerShape(roundedCornerSize)),
        color = surfaceColor
    ) {
        Row {
            Row(Modifier.fillMaxHeight().weight(3f)) {
                Column(
                    Modifier
                        .fillMaxHeight()
                        .weight(3f)
                        .padding(
                            start = roundedCornerSize + 15.dp,
                            top = 10.dp,
                            bottom = 10.dp
                        )
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
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    IconButton(modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                        onClick = refreshAmountInWallet) {
                        Icon(
                            modifier = Modifier
                                .scale(
                                    1.1f,
                                    1.1f
                                )
                                .scale(
                                    -1f,
                                    1f
                                )
                                .drawBehind {
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                DarkGray.copy(0.9f),
                                                Transparent
                                            ),
                                            radius = size.minDimension / 1.2f
                                        ),
                                        radius = size.maxDimension,
                                        blendMode = BlendMode.ColorDodge
                                    )
                                },
                            painter = painterResource(id = ODCIcons.refreshIcon),
                            contentDescription = null
                        )
                    }
                }
            }
            Column(
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(8.dp)
                    .aspectRatio(1f).border(Dp.Hairline, Black, CircleShape)
            ) {
                userPhotoSmallComposable()
            }
        }
    }
}


@Deprecated("Better use simple BalanceBlock")
@Preview(showBackground = true)
@Composable
fun BalanceBlockConstraintLayout(
    modifier: Modifier = Modifier,
    userInfo: UserInfo? = null,
    balance: String = "0 RUB",
    roundedCornerSizePercentage: Int = 20,
    textColor: Color = Color.White,
    surfaceColor: Color = CustomColors.Confirm_Success
) {
    Surface(
        modifier = modifier
            .requiredHeight(67.dp)
            .aspectRatio(4.6f)
            .clip(RoundedCornerShape(roundedCornerSizePercentage)),
        color = surfaceColor
    ) {
        ConstraintLayout {
            val (textBox, text1, text2, roundImage) = createRefs()
            val horizontalMiddleGuideline = createGuidelineFromTop(0.5f)
            val horizontalMainGuideline = createGuidelineFromTop(0.40f)
            val verticalSpacerGuideline = createGuidelineFromEnd(0.2f)
            val verticalStartGuideline = createGuidelineFromStart(0.07f)

            val rightImageBarrier = createEndBarrier(roundImage)
            val horImTopGL = createGuidelineFromTop(0.2f)
            val horImBotGL = createGuidelineFromBottom(0.2f)
            Box(modifier = Modifier.constrainAs(textBox) {
                start.linkTo(verticalStartGuideline)
                end.linkTo(verticalSpacerGuideline)
//                end.linkTo()
                centerAround(horizontalMainGuideline)
            }) {

            }
            Text(
                modifier = Modifier.constrainAs(text1) {
                    bottom.linkTo(horizontalMainGuideline)
                    start.linkTo(verticalStartGuideline)
                    end.linkTo(textBox.end)
                },
                text = "Your Balance",
                textAlign = TextAlign.Center,
                maxLines = 1,
                fontSize = 13.sp,
                color = textColor
            )
            Text(
                modifier = Modifier.constrainAs(text2) {
                    top.linkTo(text1.bottom)
                    start.linkTo(text1.start)

                },
                text = balance,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Visible,
                fontSize = 20.sp,
                color = textColor
            )
//            Box {
//                IconButton(onClick = { /*TODO refresh balance*/ }) {
//
//                }
//            }
            Box(
                Modifier
                    .size(50.dp)
                    .aspectRatio(1f)
                    .constrainAs(roundImage) {
                        start.linkTo(verticalSpacerGuideline)
                        centerAround(horizontalMiddleGuideline)
//                        linkTo(horImTopGL, horImBotGL)
                    }) {
//                UserPhotoSmall(modifier = Modifier.fillMaxSize())
                ResponsiveImage(
                    painter = painterResource(R.drawable.profile_pic_sample_square),
                    contentDescription = "Responsive Image",
                    relativeSize = 0.8f,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ConstraintLayoutContent() {
    Surface(
        modifier = Modifier
            .requiredHeight(67.dp)
            .aspectRatio(4.6f)
            .clip(RoundedCornerShape(percent = 15)),
        color = Color.Green
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (image) = createRefs()
            val guidelineTop = createGuidelineFromTop(0.1f)
            val guidelineBottom = createGuidelineFromTop(0.9f)

            /*Image(
                painter = painterResource(id = R.drawable.profile_pic_sample_square),
                contentDescription = "Image",
                modifier = Modifier
                    .constrainAs(image) {
                        top.linkTo(guidelineTop)
                        bottom.linkTo(guidelineBottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
//                    .fillMaxSize()
            )*/


//            Box(modifier = Modifier.size(50.dp)) {
            ResponsiveImage(
                painter = painterResource(R.drawable.profile_pic_sample_square),
                contentDescription = "Responsive Image",
                modifier = Modifier.fillMaxSize()
            )
//            }
        }
    }
}