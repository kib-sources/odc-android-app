package npo.kib.odc_demo.feature_app.data.api.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import npo.kib.odc_demo.feature_app.data.api.BankApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BankApiModule {

    @Singleton
    @Provides
    fun provideBankApi(): BankApi {
//        val baseUrl = "http://10.0.2.2:80"
//        val baseUrl = "http://192.168.0.103:5001"
        val baseUrl = "http://185.154.195.223"
        val interceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
        return Retrofit.Builder().baseUrl(baseUrl).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build().create(BankApi::class.java)
    }
}