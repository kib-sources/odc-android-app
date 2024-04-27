package npo.kib.odc_demo.common.data.util


import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren

fun myLogs(msg: Any?, tag: Any? = "myLogs") = Log.d(tag.toString(), msg.toString())

fun Any.log(
    msg: Any?, tag: String = this::class.simpleName ?: "Anonymous: ${this::class}"
) = Log.d(tag, msg.toString())

@OptIn(ExperimentalStdlibApi::class)
fun <T : Any> T.logOut(startMsg: String = "", tag: String? = null): T {
    try {
        if (this is ByteArray) {
            Log.d(
                tag ?: "logOut",
                startMsg + "This is ByteArray. toHex: " + toHexString() + "\ndecodeToString: decodeToString(throwOnInvalidSequence = true)"
            )
        }
    } catch (e: CharacterCodingException) {
        Log.d(tag ?: "logOut", "This is ByteArray. Caught CharacterCodingException")
    }
    Log.d(tag ?: "logOut", startMsg + this.toString())
    return this
}

fun Context.makeToast(text: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, text, duration).show()
}