package npo.kib.odc_demo.data

import npo.kib.odc_demo.core.Crypto
import npo.kib.odc_demo.core.getString
import npo.kib.odc_demo.core.loadPublicKey
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey

class BankRepository {

    private val okHttpClient = OkHttpClient.Builder().build()

    private val retrofit = Retrofit
        .Builder()
        .baseUrl("http://31.186.250.158:80")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(BankApi::class.java)

    suspend fun getBok(): PublicKey {
        val bokResponse = retrofit.getBok()
        return loadPublicKey(bokResponse.bok
        )
    }

    suspend fun registerWallet() {
        val sok = Crypto.getPublicKeyFromStore().getString()
        val walletResponse = retrofit.registerWallet(sok)
    }
}