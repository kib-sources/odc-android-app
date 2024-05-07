package npo.kib.odc_demo.core.wallet.model_extensions

import npo.kib.odc_demo.core.wallet.Crypto
import npo.kib.odc_demo.core.wallet.model.Banknote
import java.security.PublicKey

private fun makeBanknoteHashValue(
    currencyCode: Int, time: Int, amount: Int, bin: Int, bnid: String
): ByteArray = Crypto.hash(
    currencyCode.toString(), time.toString(), amount.toString(), bin.toString(), bnid
)

fun Banknote.verification(bok: PublicKey): Boolean {
    val hashValueCheck = makeBanknoteHashValue(currencyCode, time, amount, bin, bnid)
    return Crypto.verifySignature(hashValueCheck, signature, bok)
}

