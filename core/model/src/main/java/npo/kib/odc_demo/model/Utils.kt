package npo.kib.odc_demo.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import npo.kib.odc_demo.connectivity.bluetooth.BluetoothController
import npo.kib.odc_demo.wallet.Crypto.toHex

fun myLogs(msg: Any?, tag: Any? = "myLogs") = Log.d(tag.toString(), msg.toString())

fun Any.log(
    msg: Any?, tag: String = this::class.simpleName ?: "Anonymous: ${this::class}"
) = Log.d(tag, msg.toString())

fun <T : Any> T.logOut(startMsg: String = "", tag: String? = null): T {
    try {
        if (this is ByteArray) {
            Log.d(
                tag ?: "logOut",
                startMsg + "This is ByteArray. toHex: " + toHex() + "\ndecodeToString: decodeToString(throwOnInvalidSequence = true)"
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

fun CoroutineScope.cancelChildren() = coroutineContext.cancelChildren()

fun String.isAValidAmount(): Boolean = toIntOrNull()?.let { it > 0 } ?: false

fun String?.containsPrefix(prefix: String = BluetoothController.DEVICE_NAME_PREFIX): Boolean =
    this?.startsWith(prefix) ?: false


fun String.withoutPrefix(prefix: String = BluetoothController.DEVICE_NAME_PREFIX): String =
    substringAfter(prefix)