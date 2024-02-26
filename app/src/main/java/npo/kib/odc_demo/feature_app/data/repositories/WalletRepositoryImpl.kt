package npo.kib.odc_demo.feature_app.data.repositories

import android.util.Log
import kotlinx.coroutines.*
import npo.kib.odc_demo.feature_app.data.datastore.DefaultDataStoreKey
import npo.kib.odc_demo.feature_app.data.datastore.KeysDataStoreKey.*
import npo.kib.odc_demo.feature_app.data.db.Amount
import npo.kib.odc_demo.feature_app.data.db.BanknotesDao
import npo.kib.odc_demo.feature_app.data.db.BlockDao
import npo.kib.odc_demo.feature_app.domain.core.*
import npo.kib.odc_demo.feature_app.domain.model.connection_status.ServerConnectionStatus
import npo.kib.odc_demo.feature_app.domain.model.connection_status.ServerConnectionStatus.WALLET_ERROR
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.*
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.bank_api.WalletRequest
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.AcceptanceBlocks
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.Block
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.UserInfo
import npo.kib.odc_demo.feature_app.domain.repository.*
import java.security.PrivateKey
import java.security.PublicKey

class WalletRepositoryImpl(
    private val banknotesDao: BanknotesDao,
    private val blockDao: BlockDao,
    private val bankRepository: BankRepository,
    private val keysDataStoreRepository: KeysDataStoreRepository,
    private val defaultDataStoreRepository: DefaultDataStoreRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : WalletRepository {

    //todo better add field isRegistered : Boolean (?) here or
    // maybe inside the wallet class itself
    private var wallet: Wallet? = null

    //todo actually should not need to use Wallet directly anywhere outside,
    // only inside the repositories, so better not to return it

    override suspend fun getOrRegisterWallet(): Wallet {
        wallet?.let { return it }
        return withContext(ioDispatcher) {
            //Getting keys from KeyStore or generating new ones
            var keys: Pair<PublicKey, PrivateKey>
            try {
                keys = Crypto.getSimKeys()
            } catch (e: NullPointerException) {
                keys = Crypto.initSKP()
                keysDataStoreRepository.clear()
            }

            val sok = keys.first
            val spk = keys.second

            var sokSignature = keysDataStoreRepository.readValue(SOK_SIGN_KEY)
            var wid = keysDataStoreRepository.readValue(WID_KEY)
            var bin = keysDataStoreRepository.readValue(BIN_KEY)
            var bokString = keysDataStoreRepository.readValue(BOK_KEY)

            if (sokSignature == null || wid == null || bokString == null || bin == null) {
                Log.d("OpenDigitalCash", "getting sok_sign, wid and bok from server")
                val credentialsResponse = bankRepository.getCredentials()
                val walletResp = bankRepository.registerWallet(WalletRequest(sok.getStringPem()))
                bin = credentialsResponse.bin
                bokString = credentialsResponse.bok
                sokSignature = walletResp.sokSignature
                verifySokSign(sok, sokSignature, bokString)
                wid = walletResp.wid
                with(keysDataStoreRepository) {
                    writeValue(BIN_KEY, bin)
                    writeValue(BOK_KEY, bokString)
                    writeValue(SOK_SIGN_KEY, sokSignature)
                    writeValue(WID_KEY, wid)
                }
            }

            val wallet = Wallet(spk, sok, sokSignature, bokString.loadPublicKey(), bin.toInt(), wid)
            this@WalletRepositoryImpl.wallet = wallet
            wallet
        }
    }

    override suspend fun isWalletRegistered(): Boolean {
        return withContext(ioDispatcher) {
            val sokSignature = keysDataStoreRepository.readValue(SOK_SIGN_KEY)
            val wid = keysDataStoreRepository.readValue(WID_KEY)
            val bin = keysDataStoreRepository.readValue(BIN_KEY)
            val bokString = keysDataStoreRepository.readValue(BOK_KEY)
            !(sokSignature == null || wid == null || bokString == null || bin == null)
        }
    }

    override suspend fun getStoredInWalletSum() =
        withContext(ioDispatcher) { banknotesDao.getStoredSum() }

    override suspend fun getLocalUserInfo(): UserInfo = withContext(ioDispatcher) {
        UserInfo(
            defaultDataStoreRepository.readValue(DefaultDataStoreKey.USER_NAME)
                ?: "Unspecified User", getOrRegisterWallet().walletId
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
        return wallet.acceptanceInit(blocks, protectedBlock)
    }

    override suspend fun walletSignature(
        parentBlock: Block, childBlock: Block, protectedBlock: ProtectedBlock
    ): Block {
        val wallet = getOrRegisterWallet()
        return wallet.signature(parentBlock, childBlock, protectedBlock)
    }

    override suspend fun insertBanknote(banknoteWithProtectedBlock: BanknoteWithProtectedBlock) =
        withContext(ioDispatcher) {
            banknotesDao.insertBanknote(banknoteWithProtectedBlock)
        }


    override suspend fun getBanknotesIdsAndAmounts(): List<Amount> =
        withContext(ioDispatcher) { banknotesDao.getBnidsAndAmounts() }


    override suspend fun getBanknoteByBnid(requiredBnid: String): BanknoteWithProtectedBlock =
        withContext(ioDispatcher) { banknotesDao.getBanknoteByBnid(requiredBnid) }


    override suspend fun deleteBanknoteByBnid(bnid: String) = withContext(ioDispatcher) {
        banknotesDao.deleteBanknoteByBnid(bnid)
    }

    override suspend fun issueBanknotes(
        amount: Int
    ): ServerConnectionStatus = withContext(ioDispatcher) {
        val wallet = try {
            getOrRegisterWallet()
        } catch (e: Exception) {
            return@withContext WALLET_ERROR
        }
        return@withContext bankRepository.issueBanknotes(wallet = wallet, amount = amount,
                                                         walletInsertionCallback = { banknoteWithProtectedBlock, block ->
                                                             banknotesDao.insertBanknote(
                                                                 banknoteWithProtectedBlock
                                                             )
                                                             blockDao.insertBlock(block)
                                                         })
    }

    override suspend fun insertBlock(block: Block) = withContext(ioDispatcher) {
        blockDao.insertBlock(block)
    }

    override suspend fun getBlocksByBnid(requiredBnid: String): List<Block> =
        withContext(ioDispatcher) {
            blockDao.getBlocksByBnid(requiredBnid)
        }


    override suspend fun getBanknotesWithBlockchainByBnids(bnidList: List<String>): List<BanknoteWithBlockchain>? =
        withContext(ioDispatcher) {
            if (bnidList.isEmpty()) return@withContext null
            val resultList = bnidList.map { bnid ->
                ensureActive()
                BanknoteWithBlockchain(getBanknoteByBnid(bnid), getBlocksByBnid(bnid))
            }
            resultList.takeIf { bnidList.size == resultList.size }
        }

    override suspend fun addBanknotesToWallet(banknotes: List<BanknoteWithBlockchain>) =
        withContext(ioDispatcher) {
            banknotes.forEach {
                ensureActive()
                banknotesDao.insertBanknote(it.banknoteWithProtectedBlock)
                it.blocks.forEach { block -> ensureActive(); blockDao.insertBlock(block) }
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
        val sokHash = Crypto.hash(sok.getStringPem())
        if (!Crypto.verifySignature(sokHash, sokSignature, bokString.loadPublicKey())) {
            throw Exception("Подпись SOK недействительна")
        }
    }
}