package npo.kib.odc_demo.data.models

import kotlinx.serialization.Serializable
import npo.kib.odc_demo.core.Crypto
import java.security.PublicKey

@Serializable
data class Banknote(
    val bin: Int,
    val amount: Int,
    val currencyCode: Int,
    val bnid: String,
    val signature: String,
    val time: Int
) {
    private fun makeBanknoteHashValue(
        currencyCode: Int,
        time: Int,
        amount: Int,
        bnid: String
    ): ByteArray {
        return Crypto.hash(currencyCode.toString(), time.toString(), amount.toString(), bnid)
    }

    fun verification(bok: PublicKey): Boolean {
        val hashValueCheck = makeBanknoteHashValue(currencyCode, time, amount, bnid)
        return Crypto.verifySignature(hashValueCheck, signature, bok)
    }
}