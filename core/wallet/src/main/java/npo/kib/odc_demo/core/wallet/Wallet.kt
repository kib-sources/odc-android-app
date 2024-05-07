package npo.kib.odc_demo.core.wallet

import npo.kib.odc_demo.core.common_jvm.checkTimeIsNearCurrent
import npo.kib.odc_demo.core.common_jvm.toHex
import npo.kib.odc_demo.core.wallet.Crypto.asPemString
import npo.kib.odc_demo.core.wallet.Crypto.randomMagic
import npo.kib.odc_demo.core.wallet.model.Banknote
import npo.kib.odc_demo.core.wallet.model.ProtectedBlock
import npo.kib.odc_demo.core.wallet.model.data_packet.variants.AcceptanceBlocks
import npo.kib.odc_demo.core.wallet.model.data_packet.variants.Block
import npo.kib.odc_demo.core.wallet.model_extensions.makeBlockHashValue
import npo.kib.odc_demo.core.wallet.model_extensions.verification
import java.security.PrivateKey
import java.security.PublicKey
import java.util.Calendar
import java.util.UUID

//todo make the methods that may take time suspend.
/**
 *  The wallet implementation.
 *  In the MVP must be located on the SIM-card.
 */
class Wallet(
    private val spk: PrivateKey,
    private val sok: PublicKey,
    private val sokSignature: String,
    private val bok: PublicKey,
    private val bin: Int,
    val walletId: String //wid
) {
    private fun otokSignature(otok: PublicKey) =
        Crypto.signature(Crypto.hash(otok.asPemString()), spk)

    fun banknoteVerification(banknote: Banknote) {
        if (banknote.bin != bin) {
            throw Exception("The banknote was issued by another bank")
        }

        if (!banknote.verification(bok)) {
            throw Exception("The banknote is counterfeit")
        }
    }

    /**
     *  Probably some noticeable time consumed.
     *
     *  Todo run tests for the time taken and decide if worth making suspend
     * */
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
            transactionHash = null,
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

    /** No time consumed */
    fun firstBlockVerification(block: Block) {
        if (!block.verification(bok)) throw Exception("Incorrect Block")
    }

    private fun blockchainVerification(blocks: List<Block>) {
        var lastKey = bok
        for (block in blocks) {
            if (!block.verification(lastKey)) throw Exception("Incorrect blockchain")
            lastKey = block.otok
        }
    }


    //B
    /**
     * Create the new [ProtectedBlock] for the banknote from the old [protectedBlock] and create a new
     * [childBlock][Block] based on the last parent block from the [blocks]
     *
     * Probably some noticeable time consumed.
     *
     *   Todo need to run tests for the time taken and decide if worth making suspend
     * */
    fun acceptanceInit(
        blocks: List<Block>, protectedBlock: ProtectedBlock
    ): AcceptanceBlocks {
        // TODO blockchain verification disabled for demo
//        blockchainVerification(blocks)

        if (protectedBlock.parentSokSignature == null) {
            throw Exception("protectedBlock.parentSokSignature == null")
        }
        if (protectedBlock.parentSok == null) {
            throw Exception("protectedBlock.parentSok == null")
        }
        if (protectedBlock.parentOtokSignature == null) {
            throw Exception("protectedBlock.parentOtokSignature == null")
        }

        val parentSokHash = Crypto.hash(protectedBlock.parentSok.asPemString())
        // TODO verification disabled for demo
//        if (!Crypto.verifySignature(parentSokHash, protectedBlock.parentSokSignature, bok)) {
//            throw Exception("Некорректный soc")
//        }

        val parentBlock = blocks.last()
        // TODO verification disabled for demo
//        val otokHash = Crypto.hash(parentBlock.otok.getStringPem())
//        if (!Crypto.verifySignature(
//                otokHash,
//                protectedBlock.parentOtokSignature,
//                protectedBlock.parentSok
//            )
//        ) {
//            throw Exception("Некорректный parent otok")
//        }

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
            transactionHash = null,
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

    /**
     *  Verifying
     *  - That the new block is created within epsilon seconds prior to the current time
     *  - Protected block's signature,
     *  - Child block's signature
     * */
    private fun acceptanceInitVerification(
        childBlock: Block, protectedBlock: ProtectedBlock, bok: PublicKey
    ) {
        checkTimeIsNearCurrent(childBlock.time, 300).getOrThrow()

        val sokHash = Crypto.hash(protectedBlock.sok!!.asPemString())
        if (!Crypto.verifySignature(sokHash, protectedBlock.sokSignature!!, bok)) {
            throw Exception("soc signed not by the bank")
        }

        val otokHash = Crypto.hash(childBlock.otok.asPemString())
        if (!Crypto.verifySignature(otokHash, protectedBlock.otokSignature, protectedBlock.sok)) {
            throw Exception("otok defined not by the SIM card")
        }
    }

    //A
    /**
     *  Probably some noticeable time consumed.
     *
     *   Todo need to run tests for the time taken and decide if worth making suspend
     * */
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
            transactionHash = hashValue.toHex(),
            transactionHashSignature = signature,
        )
    }
}