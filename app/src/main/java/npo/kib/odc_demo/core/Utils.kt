package npo.kib.odc_demo.core

import android.widget.Toast
import androidx.fragment.app.Fragment
import java.util.*
import kotlin.random.Random

fun randomMagic(): String = (1..15).asSequence()
    .map { Random.nextInt(0, 10) }
    .map { it.toString() }
    .reduce { acc, it -> acc + it }


fun checkHashes(hash1: ByteArray, hash2: ByteArray): Boolean {
    if (hash1.count() != hash2.count()) {
        return false
    }
    return hash1.zip(hash2).all { (first, second) -> first == second }
}

fun checkTimeIsNearCurrent(t: Int, epsilon: Int): Boolean {
    val timestamp = Calendar.getInstance().timeInMillis / 1000
    val diff = timestamp - t
    return (diff in 0..epsilon)
}

fun String.decodeHex(): ByteArray {
    check(length % 2 == 0) { "Must have an even length" }

    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}
