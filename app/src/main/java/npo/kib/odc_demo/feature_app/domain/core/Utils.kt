package npo.kib.odc_demo.feature_app.domain.core

import java.util.Calendar
import kotlin.Result.Companion
import kotlin.random.Random

fun randomMagic(): String = (1..15).asSequence()
    .map { Random.nextInt(0, 10) }
    .map { it.toString() }
    .reduce { acc, it -> acc + it }


fun checkHashes(
    hash1: ByteArray, hash2: ByteArray
): Result<Boolean> {
    if (hash1.size != hash2.size) {
        return Result.failure(Exception("Hash sizes do not match. Hash1 size = ${hash1.size}, Hash2 size = ${hash2.size}"))
    }
    hash1.zip(hash2).onEach { (first, second) ->
        if (first != second) return Companion.failure(Exception("checkHashes() Byte mismatch in hashes ByteArrays, byte A = $first , byte B = $second"))
    }
    return Companion.success(true)
}

fun checkTimeIsNearCurrent(t: Int, epsilon: Int): Result<Boolean> {
    val timestamp = Calendar.getInstance().timeInMillis / 1000
    val diff = timestamp - t
    return if (diff in 0..epsilon) Result.success(true)
    else Result.failure(
        Exception("Incorrect time." +
                "\nBlock time: $t " +
                "\nCurrent time: $timestamp " +
                "\nDifference: $diff" +
                "\nEpsilon: $epsilon"))
}

fun String.decodeHex(): ByteArray {
    require(length % 2 == 0) { "Must have an even length" }
    return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}
