package npo.kib.odc_demo.data

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import npo.kib.odc_demo.core.Crypto
import npo.kib.odc_demo.core.Wallet
import npo.kib.odc_demo.core.getStringPem
import npo.kib.odc_demo.core.loadPublicKey
import npo.kib.odc_demo.data.models.WalletRequest
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.PrivateKey
import java.security.PublicKey

class WalletRepository(application: Application) {
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
        var bokString = prefs.getString(bokKey, null)

        if (sokSignature == null || wid == null || bokString == null) {
            val okHttpClient = OkHttpClient.Builder().build()
            val url = "http://31.186.250.158:80"
            val retrofit = Retrofit
                .Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BankApi::class.java)

            Log.d("OpenDigitalCash", "getting sok_sign, wid and bok from server")
            val bokResponse = retrofit.getBok()
            val walletResp =
                retrofit.registerWallet(WalletRequest(sok.getStringPem()))
            bokString = bokResponse.bok
            sokSignature = walletResp.sokSignature
            verifySokSign(sok, sokSignature, bokString)
            wid = walletResp.wid
            editor.putString(bokKey, bokString).putString(sokSignKey, sokSignature)
                .putString(widKey, wid).apply()
        }
        return Wallet(spk, sok, sokSignature, bokString.loadPublicKey(), wid)
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
        val bokString = prefs.getString(bokKey, null)
        return !(sokSignature == null || wid == null || bokString == null)
    }
}