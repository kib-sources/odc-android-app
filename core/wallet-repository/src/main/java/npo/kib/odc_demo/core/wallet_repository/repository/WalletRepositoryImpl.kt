package npo.kib.odc_demo.core.wallet_repository.repository

import android.util.Log
import kotlinx.coroutines.*
import npo.kib.odc_demo.core.common.data.util.log
import npo.kib.odc_demo.core.database.repository.BlockchainRepository
import npo.kib.odc_demo.core.datastore.DefaultDataStoreObject.USER_NAME
import npo.kib.odc_demo.core.datastore.DefaultDataStoreRepository
import npo.kib.odc_demo.core.datastore.UtilityDataStoreObject.*
import npo.kib.odc_demo.core.datastore.UtilityDataStoreRepository
import npo.kib.odc_demo.core.network.api.BankRepository
import npo.kib.odc_demo.core.model.bank_api.ServerConnectionStatus
import npo.kib.odc_demo.core.model.bank_api.ServerConnectionStatus.WALLET_ERROR
import npo.kib.odc_demo.core.model.bank_api.WalletRequest
import npo.kib.odc_demo.core.wallet.model.*
import npo.kib.odc_demo.core.wallet.Crypto
import npo.kib.odc_demo.core.wallet.Crypto.asPemString
import npo.kib.odc_demo.core.wallet.Crypto.generatePublicKey
import npo.kib.odc_demo.core.wallet.Wallet
import npo.kib.odc_demo.core.wallet.model.data_packet.variants.AcceptanceBlocks
import npo.kib.odc_demo.core.wallet.model.data_packet.variants.Block
import npo.kib.odc_demo.core.wallet.model.data_packet.variants.UserInfo
import java.security.PrivateKey
import java.security.PublicKey

class WalletRepositoryImpl(
    private val blockchainRepository: BlockchainRepository,
    private val bankRepository: BankRepository,
    private val utilityDataStoreRepository: UtilityDataStoreRepository,
    private val defaultDataStoreRepository: DefaultDataStoreRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : WalletRepository {

    private var wallet: Wallet? = null

    override suspend fun registerWallet(): Result<Boolean> {
        return try {
            getOrRegisterWallet()
            Result.success(true)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            this@WalletRepositoryImpl.log("Exception while registering wallet:\n$e")
            Result.failure(e)
        }
    }

    private suspend fun getOrRegisterWallet(): Wallet {
        wallet?.let {
            this.log("Returning existing wallet")
            return it
        }
        return withContext(ioDispatcher) {
            this@WalletRepositoryImpl.log("Registering the wallet")
            //Getting keys from KeyStore or generating new ones
            var keys: Pair<PublicKey, PrivateKey>
            try {
                keys = Crypto.getSimKeys()
            } catch (e: NullPointerException) {
                keys = Crypto.initSKP()
                utilityDataStoreRepository.clear()
            }

            val sok = keys.first
            val spk = keys.second

            var sokSignature = utilityDataStoreRepository.readValue(SOK_SIGN)
            var wid = utilityDataStoreRepository.readValue(WID)
            var bin = utilityDataStoreRepository.readValue(BIN)
            var bokString = utilityDataStoreRepository.readValue(BOK)

            if (sokSignature == null || wid == null || bokString == null || bin == null) {
                Log.d(
                    "OpenDigitalCash", "getting sok_sign, wid and bok from server"
                )
                val credentialsResponse = bankRepository.getCredentials()
                val walletResp = bankRepository.registerWallet(WalletRequest(sok.asPemString()))
                bin = credentialsResponse.bin
                bokString = credentialsResponse.bok
                sokSignature = walletResp.sokSignature
                verifySokSign(
                    sok, sokSignature, bokString
                )
                wid = walletResp.wid
                with(utilityDataStoreRepository) {
                    writeValue(BIN, bin)
                    writeValue(BOK, bokString)
                    writeValue(SOK_SIGN, sokSignature)
                    writeValue(WID, wid)
                }
            }

            val wallet = Wallet(spk, sok, sokSignature, bokString.generatePublicKey(), bin.toInt(), wid)
            this@WalletRepositoryImpl.wallet = wallet
            wallet
        }
    }

    override suspend fun isWalletRegistered(): Boolean {
        return withContext(ioDispatcher) {
            val sokSignature = utilityDataStoreRepository.readValue(SOK_SIGN)
            val wid = utilityDataStoreRepository.readValue(WID)
            val bin = utilityDataStoreRepository.readValue(BIN)
            val bokString = utilityDataStoreRepository.readValue(BOK)
            !(sokSignature == null || wid == null || bokString == null || bin == null)
        }
    }

    override suspend fun getStoredInWalletSum() =
        withContext(ioDispatcher) { blockchainRepository.getStoredSum() }

    override suspend fun getWalletId(): String? = wallet?.walletId

    override suspend fun getLocalUserInfo(): UserInfo = withContext(ioDispatcher) {
        UserInfo(
            defaultDataStoreRepository.readValueOrDefault(USER_NAME), getOrRegisterWallet().walletId
        )
    }

    override suspend fun walletBanknoteVerification(banknote: Banknote) {
        val wallet = getOrRegisterWallet()
        wallet.banknoteVerification(banknote)
    }

    override suspend fun walletFirstBlock(banknote: Banknote): Pair<Block, ProtectedBlock> {
        val wallet = getOrRegisterWallet()
        return wallet.firstBlock(banknote)
    }

    override suspend fun walletFirstBlockVerification(block: Block) {
        val wallet = getOrRegisterWallet()
        wallet.firstBlockVerification(block)
    }

    override suspend fun walletInitProtectedBlock(protectedBlock: ProtectedBlock): ProtectedBlock {
        val wallet = getOrRegisterWallet()
        return wallet.initProtectedBlock(protectedBlock)
    }


    /**Create the new [ProtectedBlock] for the banknote from the old [protectedBlock] and create a new
     * [childBlock][Block] based on the last parent block from the [blocks]*/
    override suspend fun walletAcceptanceInit(
        blocks: List<Block>, protectedBlock: ProtectedBlock
    ): AcceptanceBlocks {
        val wallet = getOrRegisterWallet()
        return wallet.acceptanceInit(
            blocks, protectedBlock
        )
    }

    override suspend fun walletSignature(
        parentBlock: Block, childBlock: Block, protectedBlock: ProtectedBlock
    ): Block {
        val wallet = getOrRegisterWallet()
        return wallet.signature(
            parentBlock, childBlock, protectedBlock
        )
    }

    override suspend fun insertBanknote(banknoteWithProtectedBlock: BanknoteWithProtectedBlock) =
        withContext(ioDispatcher) {
            blockchainRepository.insertBanknote(banknoteWithProtectedBlock)
        }


    override suspend fun getBanknotesIdsAndAmounts(): List<Amount> =
        withContext(ioDispatcher) { blockchainRepository.getBnidsAndAmounts() }


    override suspend fun getBanknoteByBnid(requiredBnid: String): BanknoteWithProtectedBlock =
        withContext(ioDispatcher) { blockchainRepository.getBanknoteByBnid(requiredBnid) }


    override suspend fun deleteBanknoteByBnid(bnid: String) = withContext(ioDispatcher) {
        blockchainRepository.deleteBanknoteByBnid(bnid)
    }

    override suspend fun issueBanknotes(
        amount: Int
    ): ServerConnectionStatus = withContext(ioDispatcher) {
        val wallet = try {
            getOrRegisterWallet()
        } catch (e: Exception) {
            this@WalletRepositoryImpl.log("Issuing banknotes exception: WALLET_ERROR")
            return@withContext WALLET_ERROR
        }
        return@withContext bankRepository.issueBanknotes(wallet = wallet, amount = amount,
            walletInsertionCallback = { banknoteWithProtectedBlock, block ->
                this@WalletRepositoryImpl.log("Saving received banknotes from server to wallet")
                blockchainRepository.insertBanknote(
                    banknoteWithProtectedBlock
                )
                blockchainRepository.insertBlock(block)
            })
    }

    override suspend fun insertBlock(block: Block) = withContext(ioDispatcher) {
        blockchainRepository.insertBlock(block)
    }

    override suspend fun getBlocksByBnid(requiredBnid: String): List<Block> =
        withContext(ioDispatcher) {
            blockchainRepository.getBlocksByBnid(requiredBnid)
        }


    override suspend fun getBanknotesWithBlockchainByBnids(bnidList: List<String>): List<BanknoteWithBlockchain>? =
        withContext(ioDispatcher) {
            if (bnidList.isEmpty()) return@withContext null
            val resultList = bnidList.map { bnid ->
                ensureActive()
                BanknoteWithBlockchain(
                    getBanknoteByBnid(bnid), getBlocksByBnid(bnid)
                )
            }
            resultList.takeIf { bnidList.size == resultList.size }
        }

    override suspend fun addBanknotesToWallet(banknotes: List<BanknoteWithBlockchain>) =
        withContext(ioDispatcher) {
            banknotes.forEach {
                ensureActive()
                blockchainRepository.insertBanknote(it.banknoteWithProtectedBlock)
                it.blocks.forEach { block ->
                    ensureActive(); blockchainRepository.insertBlock(
                    block
                )
                }
            }
        }


    override suspend fun deleteBanknotesWithBlockchainByBnids(bnidList: List<String>) =
        withContext(ioDispatcher) {
            bnidList.forEach {
                ensureActive()
                deleteBanknoteByBnid(it)
            }
        }


    private fun verifySokSign(
        sok: PublicKey, sokSignature: String, bokString: String
    ) {
        val sokHash = Crypto.hash(sok.asPemString())
        if (!Crypto.verifySignature(
                sokHash, sokSignature, bokString.generatePublicKey()
            )) {
            throw Exception("Подпись SOK недействительна")
        }
    }
}