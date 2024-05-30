package npo.kib.odc_demo.core.design_system.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import npo.kib.odc_demo.core.design_system.R

@Composable
fun RoundedSquareButton(modifier: Modifier = Modifier, onClick: () -> Unit, iconImageResId: Int) {
    Box(modifier = Modifier
        .size(60.dp)
        .aspectRatio(1f)
        .clip(RoundedCornerShape(percent = 20))
        .clickable { onClick() }) {
        Surface(
            color = Color.LightGray, modifier = modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = iconImageResId),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                contentDescription = "Icon"
            )
        }
    }

}


@Composable
@Preview(showBackground = true)
private fun RSBPreview(modifier: Modifier = Modifier, onClick3: () -> Unit = {}) {
    Row(
        modifier = modifier
            .requiredWidth(300.dp)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        RoundedSquareButton(
            modifier = Modifier.weight(1f),
            onClick = { },
            iconImageResId = R.drawable.atm_top_up_icon
        )
        RoundedSquareButton(
            modifier = Modifier.weight(1f), onClick = { }, iconImageResId = R.drawable.receive_money
        )
        RoundedSquareButton(
            modifier = Modifier.weight(1f),
            onClick = onClick3,
            iconImageResId = R.drawable.send_money
        )
    }
}

@Composable
fun P2PSelectionButtonRow(
    modifier: Modifier = Modifier,
    onclickATM: () -> Unit = {},
    onclickReceive: () -> Unit = {},
    onClickSend: () -> Unit = {}
) {
    Row(
        modifier = modifier.requiredSize(width = 300.dp, height = 80.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        RoundedSquareButton(
            modifier = Modifier.weight(1f),
            onClick = onclickATM,
            iconImageResId = R.drawable.atm_top_up_icon
        )
        RoundedSquareButton(
            modifier = Modifier.weight(1f),
            onClick = onclickReceive,
            iconImageResId = R.drawable.receive_money
        )
        RoundedSquareButton(
            modifier = Modifier.weight(1f),
            onClick = onClickSend,
            iconImageResId = R.drawable.send_money
        )
    }
}