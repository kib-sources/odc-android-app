package npo.kib.odc_demo.feature_app.presentation.common.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MultiplePermissionsRequestBlock(
    modifier: Modifier = Modifier,
    permissionsRequestText: String,
    onRequestPermissionsClick: () -> Unit
) {
    Column(modifier = modifier) {
        Text(text = permissionsRequestText)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRequestPermissionsClick) {
            Text("Request permissions")
        }
    }
}