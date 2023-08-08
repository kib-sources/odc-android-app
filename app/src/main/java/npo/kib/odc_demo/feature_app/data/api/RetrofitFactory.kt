package npo.kib.odc_demo.feature_app.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {

    private const val URL = "http://10.0.2.2:80"

    private var bankApi: BankApi? = null

    @Synchronized
    fun getBankApi(): BankApi {
        if (bankApi == null) {
            val interceptor = HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BODY
            }
            val okHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
            bankApi = Retrofit.Builder().baseUrl(URL).client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create()).build()
                .create(BankApi::class.java)
        }
        return bankApi as BankApi
    }
}