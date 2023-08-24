package npo.kib.odc_demo.feature_app.presentation.common.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import npo.kib.odc_demo.R
import npo.kib.odc_demo.ui.theme.CustomColors
import npo.kib.odc_demo.ui.theme.RobotoFont

@Preview(showBackground = true)
@Composable
fun BalanceBlock(
    modifier: Modifier = Modifier,
    roundedCornerSize: Dp = 15.dp,
    userPhotoSmallComposable: @Composable () -> Unit = { UserPhotoSmall() },
    textColor: Color = Color.White,
    surfaceColor: Color = CustomColors.Confirm_Success
) {
    Surface(
        modifier = modifier
            .requiredHeight(67.dp)
            .aspectRatio(4.6f)
            .clip(RoundedCornerShape(roundedCornerSize)), color = surfaceColor
    ) {
        Row {
            Column(
                Modifier
                    .fillMaxHeight()
                    .weight(3f)
                    .padding(start = roundedCornerSize, top = 10.dp, bottom = 10.dp)
//                    .padding(14.dp)
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
                    text = "10 000 RUB",
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    fontSize = 24.sp,
                    color = textColor
                )
            }
            Spacer(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            Column(
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(end = roundedCornerSize)
                    .aspectRatio(1f)
            ) {
                userPhotoSmallComposable()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BalanceBlockConstraintLayout(
    modifier: Modifier = Modifier,
    roundedCornerSizePercentage: Int = 20,
    textColor: Color = Color.White,
    surfaceColor: Color = CustomColors.Confirm_Success
) {
    Surface(
        modifier = Modifier
            .requiredHeight(67.dp)
            .aspectRatio(4.6f)
            .clip(RoundedCornerShape(roundedCornerSizePercentage)), color = surfaceColor
    ) {
        ConstraintLayout {
            val (text1, text2, roundImage) = createRefs()
            val horizontalMiddleGuideline = createGuidelineFromTop(0.5f)
            val horizontalMainGuideline = createGuidelineFromTop(0.40f)
            val verticalSpacerGuideline = createGuidelineFromEnd(0.2f)
            val verticalStartGuideline = createGuidelineFromStart(0.07f)

            val rightImageBarrier = createEndBarrier(roundImage)
            val horImTopGL = createGuidelineFromTop(0.2f)
            val horImBotGL = createGuidelineFromBottom(0.2f)

            Text(
                modifier = Modifier.constrainAs(text1) {
                    bottom.linkTo(horizontalMainGuideline)
                    start.linkTo(verticalStartGuideline)
                },
                text = "Your Balance",
                textAlign = TextAlign.Center,
                maxLines = 1,
                fontSize = 13.sp,
                color = textColor
            )
            Text(
                modifier = Modifier.constrainAs(text2) {
                    top.linkTo(horizontalMainGuideline)
                    start.linkTo(verticalStartGuideline)
                },
                text = "10 000 RUB",
                textAlign = TextAlign.Center,
                maxLines = 1,
                fontSize = 24.sp,
                color = textColor
            )
            Box(
                modifier
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
            .clip(RoundedCornerShape(percent = 15)), color = Color.Green
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