package npo.kib.odc_demo.feature_app.domain.model.serialization.serializable

import kotlinx.serialization.Serializable
import npo.kib.odc_demo.feature_app.domain.core.Crypto
import java.security.PublicKey

@Serializable
data class Banknote(
    val bin: Int,
    val amount: Int,
    val code: Int, // currencyCode
    val bnid: String,
    val signature: String,
    val time: Int
) {
    private fun makeBanknoteHashValue(
        currencyCode: Int,
        time: Int,
        amount: Int,
        bin: Int,
        bnid: String
    ): ByteArray {
        return Crypto.hash(currencyCode.toString(), time.toString(), amount.toString(), bin.toString(), bnid)
    }

    fun verification(bok: PublicKey): Boolean {
        val hashValueCheck = makeBanknoteHashValue(code, time, amount, bin, bnid)
        return Crypto.verifySignature(hashValueCheck, signature, bok)
    }
}