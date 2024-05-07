package npo.kib.odc_demo.core.wallet.model_extensions

import npo.kib.odc_demo.core.common.data.util.log
import npo.kib.odc_demo.core.common_jvm.checkByteArraysEqual
import npo.kib.odc_demo.core.wallet.Crypto.asPemString
import npo.kib.odc_demo.core.wallet.model.data_packet.variants.Block
import java.security.PublicKey

fun Block.makeBlockHashValue(): ByteArray {
    return if (parentUuid == null) {
        npo.kib.odc_demo.core.wallet.Crypto.hash(
            uuid.toString(), otok.asPemString(), bnid, time.toString()
        )
    } else {
        npo.kib.odc_demo.core.wallet.Crypto.hash(
            uuid.toString(), parentUuid.toString(), otok.asPemString(), bnid, time.toString()
        )
    }
}

@OptIn(ExperimentalStdlibApi::class)
fun Block.verification(publicKey: PublicKey): Boolean {
    // publicKey -- otok or bok
    if (magic == null) {
        throw Exception("BlockEntity not fully defined: \"magic\" missing.")
    }
    if (transactionHash == null) {
        throw Exception("BlockEntity not fully defined: \"hashValue\" missing.")
    }
    if (transactionHashSignature == null) {
        throw Exception("BlockEntity not fully defined: \"signature\" missing.")
    }

    val hashValueCheck = makeBlockHashValue()
    this.log("local hash size: " + hashValueCheck.size)
    this.log("received hash .hexToByteArray: " + transactionHash.hexToByteArray())
    this.log("received hash .hexToByteArray.size: " + transactionHash.hexToByteArray().size)
    this.log(
        "local hash: ${hashValueCheck/*.toHex()*/.toHexString()}" + "\nreceived hash: $transactionHash"
    )
    checkByteArraysEqual(hashValueCheck, transactionHash.hexToByteArray()).onFailure {
        this@Block.log(it)
        throw it
    }

    return npo.kib.odc_demo.core.wallet.Crypto.verifySignature(
        transactionHash.hexToByteArray(), transactionHashSignature, publicKey
    )
}