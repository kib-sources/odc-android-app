package npo.kib.odc_demo.feature_app.presentation.common.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import npo.kib.odc_demo.R
import npo.kib.odc_demo.ui.ThemePreviews
import npo.kib.odc_demo.ui.theme.ODCAppTheme

@ThemePreviews
@Composable
fun ODCTopBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(
                    1f,
                    fill = true
                )
                .align(Alignment.CenterVertically)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_1),
                contentDescription = "logo_1",
                modifier = Modifier.align(CenterHorizontally)
            )
        }
        Column(
            modifier = Modifier
                .weight(
                    3f,
                    fill = true
                )
                .align(Alignment.Bottom)
        ) {
            Row(modifier = Modifier.offset(y = (-5).dp)) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "OpenDigitalCash",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(
                    1f,
                    fill = true
                )
                .align(Alignment.CenterVertically)
        ) {}
    }
}

@ThemePreviews
@Composable
private fun ODCTopBarWithBalanceBlock(modifier: Modifier = Modifier) {
    ODCAppTheme {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            ODCTopBar(modifier = Modifier.weight(1f))
//        Spacer(modifier = Modifier.weight(0.5f))
            BalanceBlockConstraintLayout(
                modifier = Modifier
                    .align(CenterHorizontally)
                    .weight(1f)
            )
        }
    }
}