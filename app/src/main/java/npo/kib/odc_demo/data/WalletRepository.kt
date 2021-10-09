package npo.kib.odc_demo.data

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import npo.kib.odc_demo.R
import npo.kib.odc_demo.core.Crypto
import npo.kib.odc_demo.core.Wallet
import npo.kib.odc_demo.core.getStringPem
import npo.kib.odc_demo.core.loadPublicKey
import npo.kib.odc_demo.data.api.RetrofitFactory
import npo.kib.odc_demo.data.db.BlockchainDatabase
import npo.kib.odc_demo.data.models.WalletRequest
import java.security.PrivateKey
import java.security.PublicKey

class WalletRepository(application: Application) {
    private val binKey = "BIN"
    private val bokKey = "BOK"
    private val sokSignKey = "SOK_signed"
    private val widKey = "WID"

    private val keysPrefs = application.getSharedPreferences("openKeys", AppCompatActivity.MODE_PRIVATE)

    private val usernameKey = application.resources.getString(R.string.username_key)
    private val defaultUsername = application.resources.getString(R.string.default_username)
    private val userPrefs = PreferenceManager.getDefaultSharedPreferences(application)
    val userName = userPrefs.getString(usernameKey, defaultUsername) ?: defaultUsername

    private val blockchainDao = BlockchainDatabase.getInstance(application).blockchainDao()

    private var _wallet: Wallet? = null

    suspend fun getOrRegisterWallet(): Wallet {
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
            val bankApi = RetrofitFactory.getBankApi()
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

    fun isWalletRegistered(): Boolean {
        val sokSignature = keysPrefs.getString(sokSignKey, null)
        val wid = keysPrefs.getString(widKey, null)
        val bin = keysPrefs.getString(binKey, null)
        val bokString = keysPrefs.getString(bokKey, null)
        return !(sokSignature == null || wid == null || bokString == null || bin == null)
    }

    suspend fun getStoredInWalletSum() = blockchainDao.getStoredSum()

    private fun verifySokSign(sok: PublicKey, sokSignature: String, bokString: String) {
        val sokHash =
            Crypto.hash(sok.getStringPem())
        if (!Crypto.verifySignature(sokHash, sokSignature, bokString.loadPublicKey())) {
            throw Exception("Подпись SOK недействительна")
        }
    }
}