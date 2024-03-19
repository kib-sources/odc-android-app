package npo.kib.odc_demo.feature_app.domain.core

import android.util.Log
import java.util.*
import kotlin.random.Random

fun randomMagic(): String = (1..15).asSequence()
    .map { Random.nextInt(0, 10) }
    .map { it.toString() }
    .reduce { acc, it -> acc + it }


fun checkHashes(
    hash1: ByteArray,
    hash2: ByteArray
): Boolean {
    if (hash1.size != hash2.size) {
        Log.d(
            "checkHashes()",
            "Hash sizes do not match. Hash1 size = ${hash1.size}, Hash2 size = ${hash2.size}"
        )
        return false
    }
    return hash1.zip(hash2).all { (first, second) ->
        Log.d("checkHashes()", "Pair: First = $first ; Second = $second")
        first == second
    }
}

fun checkTimeIsNearCurrent(t: Int, epsilon: Int): Boolean {
    val timestamp = Calendar.getInstance().timeInMillis / 1000
    val diff = timestamp - t
    return (diff in 0..epsilon)
}

fun String.decodeHex(): ByteArray {
    require(length % 2 == 0) { "Must have an even length" }

    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}
