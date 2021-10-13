package npo.kib.odc_demo.core

/*
    Модуль, реализующий кошелёк.
    В МЖП (Минимально жизнеспособном продукте) должен быть в защищёной части телефона (SIM карта)
    SIC!
    В рамках презентации -- внутри самого приложения, что не безопасно .
 */

import java.security.PrivateKey
import java.security.PublicKey
import java.util.*
import npo.kib.odc_demo.core.models.Banknote
import npo.kib.odc_demo.core.models.Block
import npo.kib.odc_demo.core.models.AcceptanceBlocks
import npo.kib.odc_demo.core.models.ProtectedBlock

class Wallet(
    private val spk: PrivateKey,
    private val sok: PublicKey,
    private val sokSignature: String,
    private val bok: PublicKey,
    private val bin: Int,
    val wid: String
) {
    private fun otokSignature(otok: PublicKey) =
        Crypto.signature(Crypto.hash(otok.getStringPem()), spk)

    fun banknoteVerification(banknote: Banknote) {
        if (banknote.bin != bin) {
            throw Exception("Банкнота выпущена другим банком")
        }

        if (!banknote.verification(bok)) {
            throw Exception("Банкнота поддельная")
        }
    }

    fun firstBlock(banknote: Banknote): Pair<Block, ProtectedBlock> {
        val uuid = UUID.randomUUID()
        val otok = Crypto.initOTKP(uuid)
        val block = Block(
            uuid = uuid,
            parentUuid = null,
            bnid = banknote.bnid,
            otok = otok,
            time = banknote.time,
            magic = null,
            transactionHashValue = null,
            transactionHashSignature = null,
        )

        val transactionHash = block.makeBlockHashValue()
        val transactionHashSign = Crypto.signature(transactionHash, this.spk)
        val protectedBlock = ProtectedBlock(
            parentSok = null,
            parentSokSignature = null,
            parentOtokSignature = null,
            refUuid = uuid,
            sok = this.sok,
            sokSignature = this.sokSignature,
            otokSignature = otokSignature(otok),
            transactionSignature = transactionHashSign,
            time = banknote.time
        )
        return Pair(block, protectedBlock)
    }

    //A
    fun initProtectedBlock(protectedBlock: ProtectedBlock) = ProtectedBlock(
        parentSok = protectedBlock.sok,
        parentSokSignature = protectedBlock.sokSignature,
        parentOtokSignature = protectedBlock.otokSignature,
        refUuid = null,
        sok = null,
        sokSignature = null,
        otokSignature = "",
        transactionSignature = "",
        time = (Calendar.getInstance().timeInMillis / 1000).toInt()
    )

    fun firstBlockVerification(block: Block) {
        if (!block.verification(bok))
            throw Exception("Некорректный Block")
    }

    private fun blockchainVerification(blocks: List<Block>) {
        var lastKey = bok
        for (block in blocks) {
            if (!block.verification(lastKey))
                throw Exception("Некорректный blockchain")
            lastKey = block.otok
        }
    }

    //B
    fun acceptanceInit(blocks: List<Block>, protectedBlock: ProtectedBlock): AcceptanceBlocks {
        blockchainVerification(blocks)

        if (protectedBlock.parentSokSignature == null) {
            throw Exception("protectedBlock.parentSokSignature == null")
        }
        if (protectedBlock.parentSok == null) {
            throw Exception("protectedBlock.parentSok == null")
        }
        if (protectedBlock.parentOtokSignature == null) {
            throw Exception("protectedBlock.parentOtokSignature == null")
        }

        val parentSokHash =
            Crypto.hash(protectedBlock.parentSok.getStringPem())
        if (!Crypto.verifySignature(parentSokHash, protectedBlock.parentSokSignature, bok)) {
            throw Exception("Некорректный soc")
        }

        val parentBlock = blocks.last()
        val otokHash = Crypto.hash(parentBlock.otok.getStringPem())
        if (!Crypto.verifySignature(
                otokHash,
                protectedBlock.parentOtokSignature,
                protectedBlock.parentSok
            )
        ) {
            throw Exception("Некорректный parent otok")
        }

        // ------------------------------------------------------------------------------------------------------------
        // Теперь нужно создать новый блок
        val uuid = UUID.randomUUID()
        val otok = Crypto.initOTKP(uuid)
        val childBlock = Block(
            uuid = uuid,
            parentUuid = parentBlock.uuid,
            bnid = parentBlock.bnid,
            otok = otok,
            time = protectedBlock.time,
            magic = null,
            transactionHashValue = null,
            transactionHashSignature = null,
        )

        val transactionHash = childBlock.makeBlockHashValue()
        val transactionHashSign = Crypto.signature(transactionHash, spk)
        val protectedBlockNew = ProtectedBlock(
            parentSok = protectedBlock.parentSok,
            parentSokSignature = protectedBlock.parentSokSignature,
            parentOtokSignature = protectedBlock.parentOtokSignature,
            refUuid = uuid,
            sok = sok,
            sokSignature = sokSignature,
            otokSignature = otokSignature(otok),
            transactionSignature = transactionHashSign,
            time = protectedBlock.time
        )
        return AcceptanceBlocks(childBlock, protectedBlockNew)
    }

    private fun acceptanceInitVerification(
        childBlock: Block,
        protectedBlock: ProtectedBlock,
        bok: PublicKey
    ) {
        if (!checkTimeIsNearCurrent(childBlock.time, 60)) {
            throw Exception("Некорректное время")
        }

        val sokHash = Crypto.hash(protectedBlock.sok!!.getStringPem())
        if (!Crypto.verifySignature(sokHash, protectedBlock.sokSignature!!, bok)) {
            throw Exception("soc не подписан банком")
        }

        val otokHash = Crypto.hash(childBlock.otok.getStringPem())
        if (!Crypto.verifySignature(otokHash, protectedBlock.otokSignature, protectedBlock.sok)) {
            throw Exception("otok задан не SIM картой")
        }
    }

    //A
    fun signature(parentBlock: Block, childBlock: Block, protectedBlock: ProtectedBlock): Block {
        acceptanceInitVerification(childBlock, protectedBlock, bok)

        val magic = randomMagic()
        val hashValue = childBlock.makeBlockHashValue()
        val uuid = parentBlock.uuid
        val otpk = Crypto.getOtpk(uuid)
        val signature = Crypto.signature(hashValue, otpk)
        Crypto.deleteOneTimeKeys(uuid)
        return Block(
            uuid = childBlock.uuid,
            parentUuid = childBlock.parentUuid,
            bnid = childBlock.bnid,
            otok = childBlock.otok,
            time = childBlock.time,
            magic = magic,
            transactionHashValue = hashValue,
            transactionHashSignature = signature,
        )
    }
}