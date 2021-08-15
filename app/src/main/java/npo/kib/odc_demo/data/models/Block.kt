/*
   Декларирование одного блока
 */

package npo.kib.odc_demo.data.models

import kotlinx.serialization.Serializable
import npo.kib.odc_demo.core.Crypto
import npo.kib.odc_demo.core.checkHashes
import npo.kib.odc_demo.data.p2p.PublicKeySerializer
import npo.kib.odc_demo.data.p2p.UUIDSerializer
import java.lang.Exception
import java.security.PublicKey
import java.util.*


fun makeBlockHashValue(uuid: UUID?, parentUuid: UUID?, bnid: String, magic: String): ByteArray {
    return if (parentUuid == null) {
        Crypto.hash(uuid.toString(), bnid, magic)
    } else {
        Crypto.hash(uuid.toString(), parentUuid.toString(), bnid, magic)
    }
}

@Serializable
data class Block(
    /*
    Блок публичного блокчейна к каждой банкноте
    */
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID?,

    @Serializable(with = UUIDSerializer::class)
    val parentUuid: UUID?,

    // BankNote id
    val bnid: String,

    // One Time Open key
    @Serializable(with = PublicKeySerializer::class)
    val otok: PublicKey?,

    /// --->
    /// signature :
    val magic: String?,
//        val subscribeTransactionHash: ByteArray?,
//        val subscribeTransactionSignature: String?,
    val transactionHashValue: ByteArray?,
    val transactionHashSignature: String?,
) {
    // TODO функция отображения в JSON для передачи на сервер


    public val _hashOtok: ByteArray
        get() {
            return Crypto.hash(this.otok.toString())
        }

    fun verification(publicKey: PublicKey): Boolean {
        // publicKey -- otok or bok
        if (magic == null) {
            throw Exception("Блок не до конца определён. Не задан magic")
        }
        if (transactionHashValue == null) {
            throw Exception("Блок не до конца определён. Не задан hashValue")
        }
        if (transactionHashSignature == null) {
            throw Exception("Блок не до конца определён. Не задан signature")
        }
        val hashValueCheck = makeBlockHashValue(uuid, parentUuid, bnid, magic)
        if (!checkHashes(hashValueCheck, transactionHashValue)) {
            throw Exception("Некорректно подсчитан hashValue")
        }
        return Crypto.verifySignature(transactionHashValue, transactionHashSignature, publicKey)
    }

}

@Serializable
data class ProtectedBlock(
    /*
    Сопроваждающий блок для дополнительного подтверждения на Сервере.
    */

    @Serializable(with = PublicKeySerializer::class)
    val parentSok: PublicKey?,
    val parentSokSignature: String?,
    val parentOtokSignature: String?,


    // Ссылка на Block
    @Serializable(with = UUIDSerializer::class)
    val refUuid: UUID?,

    @Serializable(with = PublicKeySerializer::class)
    val sok: PublicKey?,
    val sokSignature: String?,
    val otokSignature: String,
    val transactionSignature: String


) {
    // TODO функция отображения в JSON для передачи на сервер

    public val _hashParentSok: ByteArray
        get() {
            return Crypto.hash(this.parentSok.toString())
        }
}

fun blockChain2Json(blockChain: List<Block>): String {
    // TODO написать core.data.blockChain2Json
    throw NotImplementedError("функция core.data.blockChain2Json ещё не написана!")
}