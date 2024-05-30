package npo.kib.odc_demo.core.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


//todo add prompt "are you sure?" before cancellation
@Composable
fun CancelTransactionBlock(onCancelClick: () -> Unit) {
    Button(onClick = onCancelClick) {
        Text(text = "Cancel the transaction")
    }
}