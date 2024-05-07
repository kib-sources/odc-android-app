package npo.kib.odc_demo.feature.history.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import npo.kib.odc_demo.core.design_system.ui.theme.CustomColors
import npo.kib.odc_demo.core.design_system.ui.theme.ODCAppTheme
import npo.kib.odc_demo.core.ui.components.asOdcIcon
import npo.kib.odc_demo.core.ui.icon.Icon
import npo.kib.odc_demo.core.ui.icon.Icon.DrawableResourceIcon
import npo.kib.odc_demo.core.ui.icon.Icon.ImageVectorIcon
import npo.kib.odc_demo.core.ui.icon.ODCIcon
import java.util.Locale
import kotlin.math.abs


@Composable
fun TransactionItem(
    modifier: Modifier = Modifier,
    amount: Int, // amount param should always be > 0. Abs() is taken. isReceived affects the sign
    date: LocalDate,
    time: LocalTime,
    isReceived: Boolean = true,
    isWithAtm: Boolean = false,
    name: String? = null,
    wid: String? = null
) {
    val icon: Icon = when (isWithAtm) {
        true -> DrawableResourceIcon(ODCIcon.P2PATMIcon.resId)
        false -> ImageVectorIcon(Icons.Default.PersonOutline)
    }
    val amountColor = when (isReceived) {
        true -> CustomColors.Confirm_Success
        false -> CustomColors.Cancel_Error
    }
    val amountString by remember {
        derivedStateOf {
            abs(amount).let {
                when (isReceived) {
                    true -> "+$it"
                    false -> "-$it"
                }
            }
        }
    }
    val currencyString = "RUB"
    val rowHeight = 80.dp
    val iconSize = 45.dp
    val dividerHeightPercent = 0.6f
    val regularTextColor = MaterialTheme.colorScheme.onBackground
    Row(
        modifier
            .heightIn(
                min = iconSize,
                max = rowHeight
            )
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 1.dp)
            .clip(RoundedCornerShape(5)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon.asOdcIcon(Modifier.requiredSize(iconSize))
        VerticalDivider(Modifier.fillMaxHeight(dividerHeightPercent).padding(start = 5.dp, end = 10.dp))
        Column( //Amount and date block
            Modifier.fillMaxHeight(0.7f),
            verticalArrangement = Arrangement.Center
        ) {
            Row( //Amount and currency block
                Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text( //Amount
                    text = amountString,
                    color = amountColor,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text( //Currency
                    text = currencyString,
                    color = amountColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row( //Date and time block
                Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = date.toString(),
                    color = regularTextColor,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = time.toString(),
                    color = regularTextColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        if (!isWithAtm) {
            Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                VerticalDivider(Modifier.fillMaxHeight(dividerHeightPercent / 1.3f).padding(horizontal = 10.dp))
                Column(Modifier) {
                    if (!name.isNullOrBlank()) {
                        Text( //Name, to lowercase, first char uppercase
                            text = name.trim().lowercase()
                                .replaceFirstChar { it.titlecase(Locale.getDefault()) },
                            color = regularTextColor,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    if (!wid.isNullOrBlank()) {
                        Text( //Last 10 chars of wid
                            text = "WID: ..." + wid.takeLast(10),
                            color = regularTextColor,
                            style = MaterialTheme.typography.bodySmall
                        )

                    }
                }
            }
        } else {


        }
        VerticalDivider(Modifier.fillMaxHeight(dividerHeightPercent).padding(start = 10.dp, end = 5.dp))
    }
}

@Preview
@Composable
private fun TransactionItemPreview() {
    ODCAppTheme {
        val someDateTime = Instant.fromEpochSeconds(10000000).toLocalDateTime(TimeZone.UTC)
        Column(modifier = Modifier.fillMaxSize()) {
            TransactionItem(
                amount = 10,
                date = LocalDate(
                    2024,
                    5,
                    6
                ),
                time = LocalTime(
                    16,
                    1,
                    1
                ),
                name = "Alice",
                wid = "1alifasksadfkhasfklasfaaslkaslksaflkaflfsks"
            )
            TransactionItem(
                isReceived = false,
                amount = 10,
                date = LocalDate(
                    2024,
                    5,
                    6
                ),
                time = LocalTime(
                    16,
                    1,
                    1
                ),
                name = "Bob",
                wid = "lqwodfoifslkasnkxcpzxpasl"
            )
            TransactionItem(
                isWithAtm = true,
                amount = 10,
                date = LocalDate(
                    2024,
                    5,
                    6
                ),
                time = LocalTime(
                    16,
                    1,
                    1
                )
            )
            TransactionItem(
                isReceived = false,
                isWithAtm = true,
                amount = 10,
                date = LocalDate(
                    2024,
                    5,
                    6
                ),
                time = LocalTime(
                    16,
                    1,
                    1
                )
            )
            TransactionItem(
                isReceived = true,
                isWithAtm = true,
                amount = 10,
                date = someDateTime.date,
                time = someDateTime.time
            )
        }
    }
}