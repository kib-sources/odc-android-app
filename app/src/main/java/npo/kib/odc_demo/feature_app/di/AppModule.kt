package npo.kib.odc_demo.feature_app.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import npo.kib.odc_demo.feature_app.data.api.BankApi
import npo.kib.odc_demo.feature_app.data.db.BlockchainDatabase
import npo.kib.odc_demo.feature_app.data.repositories.BankRepositoryImpl
import npo.kib.odc_demo.feature_app.data.repositories.WalletRepositoryImpl
import npo.kib.odc_demo.feature_app.domain.repository.BankRepository
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

//Hint: Hilt checks if parameters for @Provides methods are provided by other
//@Provide methods and injects sequentially.
//Hint: Android Studio offers help with useful icons with tooltips on the left editor bar

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    //BlockchainDatabase with BlockDao, BanknotesDao
    @Singleton
    @Provides
    fun provideBlockchainDatabase(@ApplicationContext context: Context): BlockchainDatabase =
        Room.databaseBuilder(
            context,
            BlockchainDatabase::class.java,
            BlockchainDatabase.DATABASE_NAME
        )
            .build()

    //BankApi
    @Singleton
    @Provides
    fun provideBankApi(): BankApi {
//        val baseUrl = "http://10.0.2.2:80"
        val baseUrl = "http://192.168.0.103:5001"
        val interceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
        return Retrofit.Builder().baseUrl(baseUrl).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build().create(BankApi::class.java)
    }

    //WalletRepository
    @Singleton
    @Provides
    fun provideWalletRepository(
        db: BlockchainDatabase, bankApi: BankApi, @ApplicationContext context: Context
    ): WalletRepository =
        WalletRepositoryImpl(blockchainDatabase = db, bankApi = bankApi, context = context)


    @Singleton
    @Provides
    fun provideBankRepository(
        walletRepository: WalletRepository
    ): BankRepository = BankRepositoryImpl(walletRepository = walletRepository)

}