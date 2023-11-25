package npo.kib.odc_demo.feature_app.data.repositories

import android.util.Log
import npo.kib.odc_demo.feature_app.data.datastore.KeysDataStoreKey
import npo.kib.odc_demo.feature_app.data.datastore.KeysDataStoreKey.*
import npo.kib.odc_demo.feature_app.data.db.Amounts
import npo.kib.odc_demo.feature_app.data.db.BanknotesDao
import npo.kib.odc_demo.feature_app.data.db.BlockDao
import npo.kib.odc_demo.feature_app.domain.core.Crypto
import npo.kib.odc_demo.feature_app.domain.core.Wallet
import npo.kib.odc_demo.feature_app.domain.core.getStringPem
import npo.kib.odc_demo.feature_app.domain.core.loadPublicKey
import npo.kib.odc_demo.feature_app.domain.model.connection_status.ServerConnectionStatus
import npo.kib.odc_demo.feature_app.domain.model.connection_status.ServerConnectionStatus.WALLET_ERROR
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithBlockchain
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithProtectedBlock
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.bank_api.WalletRequest
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.Block
import npo.kib.odc_demo.feature_app.domain.repository.BankRepository
import npo.kib.odc_demo.feature_app.domain.repository.DefaultDataStoreRepository
import npo.kib.odc_demo.feature_app.domain.repository.KeysDataStoreRepository
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import java.security.PrivateKey
import java.security.PublicKey

class WalletRepositoryImpl(
    private val banknotesDao: BanknotesDao,
    private val blockDao: BlockDao,
    private val bankRepository: BankRepository,
    private val keysDataStoreRepository: KeysDataStoreRepository,
    private val defaultDataStoreRepository: DefaultDataStoreRepository,
) : WalletRepository {


    private var wallet: Wallet? = null

    override suspend fun getOrRegisterWallet(): Wallet {
        wallet?.let { return it }

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
        this.wallet = wallet
        return wallet
    }

    override suspend fun isWalletRegistered(): Boolean {
        val sokSignature = keysDataStoreRepository.readValue(KeysDataStoreKey.SOK_SIGN_KEY)
        val wid = keysDataStoreRepository.readValue(KeysDataStoreKey.WID_KEY)
        val bin = keysDataStoreRepository.readValue(KeysDataStoreKey.BIN_KEY)
        val bokString = keysDataStoreRepository.readValue(KeysDataStoreKey.BOK_KEY)
        return !(sokSignature == null || wid == null || bokString == null || bin == null)
    }

    override suspend fun getStoredInWalletSum() = banknotesDao.getStoredSum()

    override suspend fun insertBanknote(banknoteWithProtectedBlock: BanknoteWithProtectedBlock) {
        banknotesDao.insertBanknote(banknoteWithProtectedBlock)
    }


    override suspend fun getBanknotesIdsAndAmounts(): List<Amounts> {
        return banknotesDao.getBnidsAndAmounts()
    }

    override suspend fun getBanknoteByBnid(requiredBnid: String): BanknoteWithProtectedBlock {
        return banknotesDao.getBanknoteByBnid(requiredBnid)
    }

    override suspend fun deleteBanknoteByBnid(bnid: String) {
        banknotesDao.deleteBanknoteByBnid(bnid)
    }


    override suspend fun issueBanknotes(
        amount: Int
    ): ServerConnectionStatus {
        val wallet = try {
            getOrRegisterWallet()
        } catch (e: Exception) {
            return WALLET_ERROR
        }
        return bankRepository.issueBanknotes(wallet = wallet,
            amount = amount,
            walletInsertionCallback = { banknoteWithProtectedBlock, block ->
                banknotesDao.insertBanknote(banknoteWithProtectedBlock)
                blockDao.insertBlock(block)
            })
    }

    override suspend fun insertBlock(block: Block) {
        blockDao.insertBlock(block)
    }

    override suspend fun getBlocksByBnid(requiredBnid: String): List<Block> {
        return blockDao.getBlocksByBnid(requiredBnid)
    }


    override suspend fun getBanknotesFromWallet(): List<BanknoteWithBlockchain> {
        TODO("Need to get banknotes from amount or return that the amount can not be constructed from available banknotes")
    }

    override suspend fun addBanknotesToWallet(banknotes: List<BanknoteWithBlockchain>) {
        TODO("add all the passed banknotes to the wallet")
    }


    private fun verifySokSign(
        sok: PublicKey,
        sokSignature: String,
        bokString: String
    ) {
        val sokHash = Crypto.hash(sok.getStringPem())
        if (!Crypto.verifySignature(sokHash, sokSignature, bokString.loadPublicKey())) {
            throw Exception("Подпись SOK недействительна")
        }
    }
}