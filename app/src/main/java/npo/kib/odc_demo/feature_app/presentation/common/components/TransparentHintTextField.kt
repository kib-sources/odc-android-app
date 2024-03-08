package npo.kib.odc_demo.feature_app.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TransparentHintTextField(
    hint: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false
) {
    var fieldText by rememberSaveable { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(true) }
    val isHintVisible by remember(
        isFocused,
        fieldText
    ) { mutableStateOf(!isFocused and fieldText.isBlank()) }
    Box(
        modifier = modifier
    ) {
        BasicTextField(
            value = fieldText,
            onValueChange = { newValue ->
                fieldText = newValue
                onValueChange(newValue)
            },
            singleLine = singleLine,
            textStyle = textStyle,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    isFocused = it.isFocused
                }
        )
        if (isHintVisible) {
            Text(modifier = Modifier.align(Alignment.TopStart),
                text = hint,
                style = textStyle,
                color = Color.DarkGray,
            )
        }
    }
}


@Preview
@Composable
private fun HintTextFieldPreview() {

        Column(modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Yellow).padding(10.dp)) {
            var res by rememberSaveable {
                mutableStateOf("")
            }
            Text(text = "Entered text: $res")
            TransparentHintTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp).border(4.dp, Color.Black, RoundedCornerShape(15)).padding(15.dp),
                hint = "Enter something...",
                onValueChange = {
                    res = it
                }
            )
            var res2 by rememberSaveable {
                mutableStateOf("")
            }
            Text(text = "Entered text2: $res2")
            TransparentHintTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp).border(4.dp, Color.Black, RoundedCornerShape(10.dp)).padding(15.dp),
                hint = "Enter something 2...",
                onValueChange = {
                    res2 = it
                }
            )
        }

}