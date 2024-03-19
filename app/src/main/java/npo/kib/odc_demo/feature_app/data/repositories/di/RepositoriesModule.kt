package npo.kib.odc_demo.feature_app.data.repositories.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import npo.kib.odc_demo.feature_app.data.api.BankApi
import npo.kib.odc_demo.feature_app.data.db.BlockchainDatabase
import npo.kib.odc_demo.feature_app.data.repositories.BankRepositoryImpl
import npo.kib.odc_demo.feature_app.data.repositories.DefaultDataStoreRepositoryImpl
import npo.kib.odc_demo.feature_app.data.repositories.UtilityDataStoreRepositoryImpl
import npo.kib.odc_demo.feature_app.data.repositories.WalletRepositoryImpl
import npo.kib.odc_demo.feature_app.domain.repository.BankRepository
import npo.kib.odc_demo.feature_app.domain.repository.DefaultDataStoreRepository
import npo.kib.odc_demo.feature_app.domain.repository.UtilityDataStoreRepository
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

    @Provides
    @Singleton
    fun provideDefaultDataStoreRepository(
        @ApplicationContext
        context: Context
    ): DefaultDataStoreRepository = DefaultDataStoreRepositoryImpl(context = context)

    @Provides
    @Singleton
    fun provideUtilityDataStoreRepository(
        @ApplicationContext
        context: Context
    ): UtilityDataStoreRepository = UtilityDataStoreRepositoryImpl(context = context)

    @Singleton
    @Provides
    fun provideBankRepository(
        bankApi: BankApi
    ): BankRepository = BankRepositoryImpl(bankApi = bankApi)

    //WalletRepository
    @Singleton
    @Provides
    fun provideWalletRepository(
        db: BlockchainDatabase,
        bankRepository: BankRepository,
        utilityDataStoreRepository: UtilityDataStoreRepository,
        defaultDataStoreRepository: DefaultDataStoreRepository
    ): WalletRepository = WalletRepositoryImpl(
        banknotesDao = db.banknotesDao,
        blockDao = db.blockDao,
        bankRepository = bankRepository,
        utilityDataStoreRepository = utilityDataStoreRepository,
        defaultDataStoreRepository = defaultDataStoreRepository
    )
}