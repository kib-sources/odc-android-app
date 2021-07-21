package npo.kib.odc_demo.data

import com.google.gson.annotations.SerializedName
import npo.kib.odc_demo.core.Crypto
import npo.kib.odc_demo.core.ISO_4217_CODE
import npo.kib.odc_demo.core.checkHashes
import java.security.PublicKey
import java.sql.Time

fun makeBanknoteHashValue(
    bin: Int,
    amount: Int,
    currencyCode: ISO_4217_CODE,
    bnid: String
): ByteArray {
    return Crypto.hash(bin.toString(), amount.toString(), currencyCode.toString(), bnid)
}

// @Serializable(with = kotlinx.serialization.json.JsonElementSerializer::class)
// @Serializable
data class Banknote(
    val bin: Int,

    @SerializedName("amount")
    val amount: Int,

    @SerializedName("code")
    val currencyCode: ISO_4217_CODE,

    // BankNote id
    @SerializedName("bnid")
    val bnid: String,

    // hash
    // val hash: ByteArray,
    val hashValue: ByteArray,
    @SerializedName("signature")
    val signature: String,

    @SerializedName("time")
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