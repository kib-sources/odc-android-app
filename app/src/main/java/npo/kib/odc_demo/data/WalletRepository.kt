package npo.kib.odc_demo.data

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import npo.kib.odc_demo.core.Crypto
import npo.kib.odc_demo.core.Wallet
import npo.kib.odc_demo.core.getStringPem
import npo.kib.odc_demo.core.loadPublicKey
import npo.kib.odc_demo.data.models.WalletRequest
import java.security.PrivateKey
import java.security.PublicKey

class WalletRepository(application: Application) {
    private val binKey = "BIN"
    private val bokKey = "BOK"
    private val sokSignKey = "SOK_signed"
    private val widKey = "WID"

    private val prefs = application.getSharedPreferences("openKeys", AppCompatActivity.MODE_PRIVATE)
    private val editor = prefs.edit()


    suspend fun getWallet(): Wallet {
        //Берём ключи из keyStore или генерируем новые
        var keys: Pair<PublicKey, PrivateKey>
        try {
            keys = Crypto.getSimKeys()
        } catch (e: NullPointerException) {
            keys = Crypto.initSKP()
            editor.clear().apply()
        }
        val sok = keys.first
        val spk = keys.second
        var sokSignature = prefs.getString(sokSignKey, null)
        var wid = prefs.getString(widKey, null)
        var bin = prefs.getString(binKey, null)
        var bokString = prefs.getString(bokKey, null)

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
            editor.putString(binKey, bin).putString(bokKey, bokString).putString(sokSignKey, sokSignature)
                .putString(widKey, wid).apply()
        }
        return Wallet(spk, sok, sokSignature, bokString.loadPublicKey(), bin.toInt(), wid)
    }

    private fun verifySokSign(sok: PublicKey, sokSignature: String, bokString: String) {
        val sokHash =
            Crypto.hash(sok.getStringPem())
        if (!Crypto.verifySignature(sokHash, sokSignature, bokString.loadPublicKey())) {
            throw Exception("Подпись SOK недействительна")
        }
    }

    fun isWalletRegistered(): Boolean {
        val sokSignature = prefs.getString(sokSignKey, null)
        val wid = prefs.getString(widKey, null)
        val bin = prefs.getString(binKey, null)
        val bokString = prefs.getString(bokKey, null)
        return !(sokSignature == null || wid == null || bokString == null || bin == null)
    }
}