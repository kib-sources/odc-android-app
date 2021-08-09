package npo.kib.odc_demo.core

/*
    Модуль, реализующий кошелёк.
    В МЖП (Минимально жизнеспособном продукте) должен быть в защищёной части телефона (SIM карта)
    SIC!
    В рамках презентации -- внутри самого приложения, что не безопасно .
 */

import android.util.Log
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*
import kotlin.collections.HashMap


import npo.kib.odc_demo.data.models.Banknote
import npo.kib.odc_demo.data.models.Block
import npo.kib.odc_demo.data.models.ProtectedBlock
import npo.kib.odc_demo.data.models.makeBlockHashValue


class Wallet(
    private val spk: PrivateKey,
    public val sok: PublicKey,
    public val sokSignature: String,

    // Сделать глобальной переменной
    public val BOK: PublicKey,
    val wid: String
) {
    private val bag = HashMap<UUID, PrivateKey>()


    var BOK_str = """-----BEGIN RSA PUBLIC KEY-----
MEgCQQCZScdB8AFwcrZDOLVsBT7m+KyuARWixZCstV99oOMYD318o0rhAqSYk/3Q
nxV31GMYcJv7qABEqnowEkTGDh1TAgMBAAE=
-----END RSA PUBLIC KEY-----""";

    // TODO определить BOK
    // var BOK = X509EncodedKeySpec()
    // var BOK = null


    // TODO зарузка в файл
    // TODO выгрузка из файла

    fun otokSignature(otok: PublicKey): String {
        return Crypto.signature(
            Crypto.hash("-----BEGIN RSA PUBLIC KEY-----\n${otok.getString()}-----END RSA PUBLIC KEY-----"),
            this.spk
        )
    }

    fun getTransactionHashSign(
        uuid: UUID,
        parentUuid: UUID?,
        otok: PublicKey,
        bnid: String,
        time: Int
    ): String {
        val hash = Crypto.hash(
            uuid.toString(),
            "-----BEGIN RSA PUBLIC KEY-----\n${otok.getString()}-----END RSA PUBLIC KEY-----",
            bnid,
            time.toString()
        )
        return Crypto.signature(hash, this.spk)
    }

    fun firstBlock(banknote: Banknote): Pair<Block, ProtectedBlock> {
        val uuid = UUID.randomUUID()
        val bnid = banknote.bnid
        val (otok, otpk) = Crypto.initOtkp()
        Log.d("OpenDigitalCashK", otok.getString())
        Log.d("OpenDigitalCashK", Crypto.getPublicKeyFromStore().getString())
        this.bag[uuid] = otpk
        val block = Block(
            uuid = uuid,
            parentUuid = null,
            bnid = bnid,
            otok = otok,
            magic = null,
            hashValue = null,
            signature = null,
        )
        val otokSignature_ = this.otokSignature(otok)
        val transactionHash = getTransactionHashSign(uuid, null, otok, bnid, banknote.time)
        val protectedBlock = ProtectedBlock(
            parentSok = null,
            parentSokSignature = null,
            parentOtokSignature = null,
            refUuid = uuid,
            sok = this.sok,
            sokSignature = this.sokSignature,
            otokSignature = otokSignature_,
            transactionSignature = transactionHash
        )

        return Pair(block, protectedBlock)
    }

    fun init(parentBlock: Block): ProtectedBlock {

        // TODO Необходимо взять parentBlock.otok и подписать его
        val parentOtokSignature = ""

        val protectedBlock = ProtectedBlock(
            parentSok = this.sok,
            parentSokSignature = this.sokSignature,
            parentOtokSignature = parentOtokSignature,
            refUuid = null,
            sok = null,
            sokSignature = null,
            otokSignature = "",
            transactionSignature = ""
        )
        return protectedBlock
    }

    fun initVerification(parentBlock: Block, protectedBlock: ProtectedBlock) {

        if (parentBlock.parentUuid == null) {
            // Передача от банка к владельцу
            throw Exception("initVerification не требуется для передачи купюры первому владельцу. См. serverInitVerification")
        }

        // Передача от владельца владельцу

        if (protectedBlock.parentOtokSignature == null) {
            throw Exception("parentOtokSignature не указан!")
        }
        if (protectedBlock.parentSokSignature == null) {
            throw Exception("parentSokSignature не указан!")
        }
        if (protectedBlock.sok == null) {
            throw Exception("parentSokSignature не указан!")
        }

        if (!Crypto.verifySignature(
                protectedBlock._hashParentSok,
                protectedBlock.parentSokSignature,
                this.BOK
            )
        ) {
            throw Exception("SOK отправителья не подписан")
        }
        if (!Crypto.verifySignature(
                parentBlock._hashOtok,
                protectedBlock.parentOtokSignature,
                protectedBlock.sok
            )
        ) {
            throw Exception("OTOK отправителья не подписан")
        }
    }

    fun acceptanceInit(
        parentBlock: Block,
        protectedBlock: ProtectedBlock,
        bok: PublicKey
    ): Pair<Block, ProtectedBlock> {


        if (protectedBlock.parentSokSignature == null) {
            throw Exception("protectedBlock.parentSokSignature == null")
        }
        if (protectedBlock.parentSok == null) {
            throw Exception("protectedBlock.parentSok == null")
        }
        if (protectedBlock.parentOtokSignature == null) {
            throw Exception("protectedBlock.parentOtokSignature == null")
        }

        if (!Crypto.verifySignature(
                Crypto.hash(protectedBlock.parentSok.toString()),
                protectedBlock.parentSokSignature,
                bok
            )
        ) {
            throw Exception("Некорректный soc")
        }

        val parentOtok = parentBlock.otok

        if (!Crypto.verifySignature(
                Crypto.hash(parentOtok.toString()),
                protectedBlock.parentOtokSignature,
                protectedBlock.parentSok
            )
        ) {
            throw Exception("Некорректный parent otok")
        }

        // ------------------------------------------------------------------------------------------------------------
        // Теперь нужно создать новый блок
        val uuid = UUID.randomUUID()

        val (otok, otpk) = Crypto.initOtkp()

        this.bag[uuid] = otpk

        val childBlock = Block(
            uuid = uuid,
            parentUuid = parentBlock.uuid,
            bnid = parentBlock.bnid,
            otok = otok,
            magic = null,
            hashValue = null,
            signature = null,
        )
        val otokSignature_ = this.otokSignature(otok)

        val protectedBlock_new = ProtectedBlock(
            parentSok = protectedBlock.parentSok,
            parentSokSignature = protectedBlock.parentSokSignature,
            parentOtokSignature = protectedBlock.parentOtokSignature,
            refUuid = uuid,
            sok = this.sok,
            sokSignature = this.sokSignature,
            otokSignature = otokSignature_,
            transactionSignature = ""
        )



        return Pair(childBlock, protectedBlock_new)
    }

    private fun acceptanceInitVerification(
        parentBlock: Block,
        childBlock: Block,
        protectedBlock: ProtectedBlock,
        bok: PublicKey
    ) {

        assert(parentBlock.uuid == childBlock.parentUuid)

        if (!Crypto.verifySignature(
                Crypto.hash(protectedBlock.sok.toString()),
                protectedBlock.sokSignature!!,
                bok
            )
        ) {
            throw Exception("soc не подписан банком")
        }

        if (!Crypto.verifySignature(
                Crypto.hash(childBlock.otok.toString()),
                protectedBlock.otokSignature!!,
                protectedBlock.sok!!,
            )
        ) {
            throw Exception("otok задан не SIM картой")
        }
    }


    fun signature(
        parentBlock: Block,
        childBlock: Block,
        protectedBlock: ProtectedBlock,
        bok: PublicKey
    ): Block {

        acceptanceInitVerification(parentBlock, childBlock, protectedBlock, bok)
        val magic = randomMagic()
        val hashValue =
            makeBlockHashValue(childBlock.uuid, childBlock.parentUuid, childBlock.bnid, magic)

        val otpk = this.bag[parentBlock.uuid]

        val signature = Crypto.signature(hashValue, otpk!!)

        val childBlock_full = Block(
            uuid = childBlock.uuid,
            parentUuid = childBlock.parentUuid,
            bnid = childBlock.bnid,
            otok = childBlock.otok,
            magic = magic,
            hashValue = hashValue,
            signature = signature,
        )

        return childBlock_full
    }
}


// Пример из POC-а
// Не используется. Написан Александром, не смотрел что там.
class WalletFromPythonPOC(
    private val spk: PrivateKey,
    val sok: PublicKey,
    val sokSignature: String,
) {

    private val bag = HashMap<UUID, PrivateKey>()

    fun newBlockParams(bnid: String, parentUuid: UUID? = null): TransactionBlock {
        val (publicKey, privateKey) = Crypto.initOtkp()

        val uuid = UUID.randomUUID()

        val transactionHash = getTransactionHash(uuid, parentUuid, publicKey, bnid)
        val initWalletSignature = Crypto.signature(transactionHash, spk)

        bag[uuid] = privateKey

        return TransactionBlock(uuid, parentUuid, publicKey, transactionHash, initWalletSignature)
    }

    fun subscribe(uuid: UUID, parentUuid: UUID, bnid: String): Subscription {
        val otpk = bag[parentUuid]
            ?: throw Exception("Уже передан блок с uuid=$parentUuid или данного блока никогда не было в кошельке")

        val magic = randomMagic()

        val transactionHash = getSubscribeTransactionHash(uuid, magic, bnid)
        val transactionSignature = Crypto.signature(transactionHash, otpk)

        // Удаляем ключ, чтобы более ни разу нельзя было подписывать
        //   в нормальном решении необходимо хранение на доверенном носителе, например на SIM
        bag.remove(parentUuid)

        return Subscription(magic, transactionHash, transactionSignature)
    }
}

data class Subscription(
    val magic: String,
    val subscribeTransactionHash: ByteArray,
    val subscribeTransactionSignature: String
)

data class TransactionBlock(
    val uuid: UUID,
    val parentUuid: UUID?,
    val otok: PublicKey,
    val transactionHash: ByteArray,
    val initWalletSignature: String
)

fun getTransactionHash(uuid: UUID, parentUuid: UUID?, otok: PublicKey, bnid: String): ByteArray =
    Crypto.hash(uuid.toString(), parentUuid.toString(), otok.toString(), bnid)

fun getSubscribeTransactionHash(uuid: UUID, magic: String, bnid: String): ByteArray =
    Crypto.hash(uuid.toString(), magic, bnid)


fun createBankSubscription(uuid: UUID, bpk: PrivateKey, bnid: String): Subscription {
    val magic = randomMagic()

    val transactionHash = getSubscribeTransactionHash(uuid, magic, bnid)
    val transactionSignature = Crypto.signature(transactionHash, bpk)

    return Subscription(magic, transactionHash, transactionSignature)
}