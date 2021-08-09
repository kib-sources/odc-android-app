package npo.kib.odc_demo.data.models

import com.google.gson.annotations.SerializedName
import npo.kib.odc_demo.core.Crypto
import npo.kib.odc_demo.core.checkHashes
import java.security.PublicKey

fun makeBanknoteHashValue(
    bin: Int,
    amount: Int,
    currencyCode: Int,
    bnid: String
): ByteArray {
    return Crypto.hash(bin.toString(), amount.toString(), currencyCode.toString(), bnid)
}

// @Serializable(with = kotlinx.serialization.json.JsonElementSerializer::class)
// @Serializable
data class Banknote(
    val bin: Int,

    val amount: Int,

    val currencyCode: Int,

    // BankNote id
    val bnid: String,

    // hash
    // val hash: ByteArray,
    val hashValue: ByteArray,
    val signature: String,

    val time: Int
) {

    // TODO сохранить в JSON
    // TODO выгрузить из JSON-а
    // TODO сохранить в protobuf
    // TODO выгрузить из protobuf


    fun verification(bok: PublicKey): Boolean {
        val checkHashValue = makeBanknoteHashValue(bin, amount, currencyCode, bnid)
        if (!checkHashes(checkHashValue, hashValue)) {
            throw Exception("HashValue не сходятся")
        }
        return Crypto.verifySignature(this.hashValue, this.signature, bok)
    }

}