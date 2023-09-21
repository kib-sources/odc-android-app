package npo.kib.odc_demo.feature_app.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import npo.kib.odc_demo.feature_app.data.api.BankApi
import npo.kib.odc_demo.feature_app.data.db.BlockchainDatabase
import npo.kib.odc_demo.feature_app.data.p2p.nearby.P2PConnectionNearbyImpl
import npo.kib.odc_demo.feature_app.data.p2p.nfc.P2PConnectionNfcImpl
import npo.kib.odc_demo.feature_app.data.repositories.BankRepositoryImpl
import npo.kib.odc_demo.feature_app.data.repositories.WalletRepositoryImpl
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnection
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnectionBidirectional
import npo.kib.odc_demo.feature_app.domain.repository.BankRepository
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.use_cases.FeatureAppUseCases
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PReceiveUseCase
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PSendUseCase
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
    fun provideBlockchainDatabase(app: Application): BlockchainDatabase =
        Room.databaseBuilder(app, BlockchainDatabase::class.java, BlockchainDatabase.DATABASE_NAME)
            .build()

    //BankApi
    @Singleton
    @Provides
    fun provideBankApi(): BankApi {
        val baseUrl = "http://10.0.2.2:80"
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
    fun provideWalletRepository(db: BlockchainDatabase,
                                bankApi: BankApi,
                                app: Application): WalletRepository =
        WalletRepositoryImpl(blockchainDao = db.banknotesDao, bankApi = bankApi, context = app)


    @Singleton
    @Provides
    fun provideBankRepository(db: BlockchainDatabase,
                              walletRepository: WalletRepository,
                              bankApi: BankApi): BankRepository = BankRepositoryImpl(
        blockchainDatabase = db,
        walletRepository = walletRepository,
        bankApi = bankApi)


    @Singleton
    @Provides
    fun provideP2PConnectionBidirectionalNearbyImpl(app: Application): P2PConnectionBidirectional =
        P2PConnectionNearbyImpl(context = app)

    @Singleton
    @Provides
    fun provideP2PConnectionNearbyImpl(app: Application): P2PConnection =
        P2PConnectionNearbyImpl(context = app)

    //other P2P connection implementations
//    @Singleton
//    @Provides
//    fun provideP2PConnectionNFCImpl(
//
//    ): P2PConnectionNfcImpl {
//
//    }

    /* @Singleton
     @Provides
     fun provideP2PConnectionWiFiDirectImpl(

     ) {
     }

     @Singleton
     @Provides
     fun provideP2PConnectionBluetoothImpl(

     ){}*/

    @Provides
    @Singleton
    fun provideSendUseCase(db: BlockchainDatabase,
                           walletRepository: WalletRepository,
                           p2p: P2PConnectionBidirectional) =
        P2PSendUseCase(blockchainDatabase = db, walletRepository = walletRepository, p2p = p2p)

    @Provides
    @Singleton
    fun provideReceiveUseCase(db: BlockchainDatabase,
                              walletRepository: WalletRepository,
                              p2p: P2PConnection) =
        P2PReceiveUseCase(blockchainDatabase = db, walletRepository = walletRepository, p2p = p2p)

//    @Provides
//    @Singleton
//    fun provideNfcReceiveUseCase(db: BlockchainDatabase,
//                                 walletRepository: WalletRepository,
//                                 p2p: P2PConnectionNfcImpl) =
//        P2PReceiveUseCase(blockchainDatabase = db, walletRepository = walletRepository,
//                          p2p = p2p)


    @Provides
    @Singleton
    fun provideAppUseCases(p2pSend: P2PSendUseCase,
                           p2pReceive: P2PReceiveUseCase): FeatureAppUseCases =
        FeatureAppUseCases(p2pSendUseCase = p2pSend, p2pReceiveUseCase = p2pReceive)
}
