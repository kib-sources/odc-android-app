package npo.kib.odc_demo.core.common_jvm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import java.util.Calendar


fun String.isAValidAmount(): Boolean = toIntOrNull()?.let { it > 0 } ?: false

fun String?.containsPrefix(prefix: String): Boolean = this?.startsWith(prefix) ?: false

fun String.withoutPrefix(prefix: String): String = substringAfter(prefix)

fun CoroutineScope.cancelChildren()  : Unit = coroutineContext.cancelChildren()

fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

fun String.decodeHex(): ByteArray {
    require(length % 2 == 0) { "Must have equal lengths" }
    return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}

fun checkByteArraysEqual(
    arr1: ByteArray, arr2: ByteArray
): Result<Boolean> {
    if (arr1.size != arr2.size) {
        return Result.failure(Exception("ByteArray sizes do not match. Arr1 size = ${arr1.size}, Arr2 size = ${arr2.size}"))
    }
    arr1.zip(arr2).onEach { (first, second) ->
        if (first != second) return Result.failure(Exception("Bytes mismatched in ByteArrays, byte A = $first , byte B = $second"))
    }
    return Result.success(true)
}


fun checkTimeIsNearCurrent(t: Int, epsilon: Int): Result<Boolean> {
    val timestamp = Calendar.getInstance().timeInMillis / 1000
    val diff = timestamp - t
    return if (diff in 0..epsilon) Result.success(true)
    else Result.failure(
        Exception("Incorrect time interval." +
                "\nEpsilon: $epsilon" +
                "\nReceived time: $t " +
                "\nCurrent time: $timestamp " +
                "\nDifference: $diff"
        ))

}