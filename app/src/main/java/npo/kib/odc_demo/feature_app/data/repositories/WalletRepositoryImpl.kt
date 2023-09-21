package npo.kib.odc_demo.feature_app.data.repositories

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import npo.kib.odc_demo.R
import npo.kib.odc_demo.common.core.Crypto
import npo.kib.odc_demo.common.core.Wallet
import npo.kib.odc_demo.common.core.getStringPem
import npo.kib.odc_demo.common.core.loadPublicKey
import npo.kib.odc_demo.feature_app.data.api.BankApi
import npo.kib.odc_demo.feature_app.data.db.BanknotesDao
import npo.kib.odc_demo.feature_app.domain.model.serialization.WalletRequest
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import java.security.PrivateKey
import java.security.PublicKey

class WalletRepositoryImpl(private val blockchainDao: BanknotesDao,
                           private val bankApi: BankApi,
                           context: Context) : WalletRepository {
    private val binKey = "BIN"
    private val bokKey = "BOK"
    private val sokSignKey = "SOK_signed"
    private val widKey = "WID"

    private val keysPrefs = context.getSharedPreferences("openKeys", AppCompatActivity.MODE_PRIVATE)

    private val usernameKey = context.resources.getString(R.string.username_key)
    private val defaultUsername = context.resources.getString(R.string.default_username)
    private val userPrefs = PreferenceManager.getDefaultSharedPreferences(context)
    override val userName = userPrefs.getString(usernameKey, defaultUsername) ?: defaultUsername

    private var _wallet: Wallet? = null

    override suspend fun getOrRegisterWallet(): Wallet {
        _wallet?.let { return it }

        //Берём ключи из keyStore или генерируем новые
        var keys: Pair<PublicKey, PrivateKey>
        try {
            keys = Crypto.getSimKeys()
        } catch (e: NullPointerException) {
            keys = Crypto.initSKP()
            keysPrefs.edit().clear().apply()
        }

        val sok = keys.first
        val spk = keys.second
        var sokSignature = keysPrefs.getString(sokSignKey, null)
        var wid = keysPrefs.getString(widKey, null)
        var bin = keysPrefs.getString(binKey, null)
        var bokString = keysPrefs.getString(bokKey, null)

        if (sokSignature == null || wid == null || bokString == null || bin == null) {
            Log.d("OpenDigitalCash", "getting sok_sign, wid and bok from server")
            val credentialsResponse = bankApi.getCredentials()
            val walletResp =
                bankApi.registerWallet(WalletRequest(sok.getStringPem()))
            bin = credentialsResponse.bin
            bokString = credentialsResponse.bok
            sokSignature = walletResp.sokSignature
            verifySokSign(sok, sokSignature, bokString)
            wid = walletResp.wid

            keysPrefs.edit().putString(binKey, bin)
                .putString(bokKey, bokString)
                .putString(sokSignKey, sokSignature)
                .putString(widKey, wid)
                .apply()
        }

        val wallet = Wallet(spk, sok, sokSignature, bokString.loadPublicKey(), bin.toInt(), wid)
        _wallet = wallet
        return wallet
    }

    override fun isWalletRegistered(): Boolean {
        val sokSignature = keysPrefs.getString(sokSignKey, null)
        val wid = keysPrefs.getString(widKey, null)
        val bin = keysPrefs.getString(binKey, null)
        val bokString = keysPrefs.getString(bokKey, null)
        return !(sokSignature == null || wid == null || bokString == null || bin == null)
    }

    override suspend fun getStoredInWalletSum() = blockchainDao.getStoredSum()

    private fun verifySokSign(sok: PublicKey, sokSignature: String, bokString: String) {
        val sokHash =
            Crypto.hash(sok.getStringPem())
        if (!Crypto.verifySignature(sokHash, sokSignature, bokString.loadPublicKey())) {
            throw Exception("Подпись SOK недействительна")
        }
    }
}