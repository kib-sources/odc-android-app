package npo.kib.odc_demo.feature_app.domain.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren

fun myLogs(msg: Any?) = Log.d("myLogs", msg.toString())

fun Context.makeToast(text: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, text, duration).show()
}

fun CoroutineScope.cancelChildren() = coroutineContext.cancelChildren()

fun String.isAValidAmount() : Boolean  = toIntOrNull()?.let { it > 0 } ?: false