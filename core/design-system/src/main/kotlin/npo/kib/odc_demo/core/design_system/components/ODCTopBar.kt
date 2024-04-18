package npo.kib.odc_demo.core.design_system.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import npo.kib.odc_demo.R
import npo.kib.odc_demo.ui.ThemePreviews
import npo.kib.odc_demo.ui.theme.ODCAppTheme

@Composable
fun ODCTopBar(modifier: Modifier = Modifier, text : String = "OpenDigitalCash") {
    Row(
        modifier = modifier
            .requiredHeight(100.dp)
            .requiredWidthIn(min = 330.dp)
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_1),
                contentDescription = "logo_1",
                modifier = Modifier.align(CenterHorizontally)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(3f)
                .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = text,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth().height(15.dp)
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.7f)
        )
    }
}

@ThemePreviews
@Composable
private fun ODCTopAppBarPreview() {
    ODCAppTheme {
        Box(Modifier.requiredWidth(400.dp)) {
            ODCTopBar()
        }
    }
}

@ThemePreviews
@Composable
private fun ODCTopBarWithBalanceBlock() {
    ODCAppTheme {
        Column(
            modifier = Modifier
                .size(
                    width = 350.dp,
                    height = 500.dp
                )
                .background(Color.DarkGray)
        ) {
            ODCTopBar(
                modifier = Modifier.align(CenterHorizontally)
                    .width(600.dp)
            )
            BalanceBlock(modifier = Modifier.align(CenterHorizontally), onWalletDetailsClick = {})
        }
    }
}