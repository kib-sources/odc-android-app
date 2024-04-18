package npo.kib.odc_demo.wallet.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import npo.kib.odc_demo.database.BlockchainDatabase
import npo.kib.odc_demo.feature_app.domain.repository.BankRepository
import npo.kib.odc_demo.feature_app.domain.repository.DefaultDataStoreRepository
import npo.kib.odc_demo.feature_app.domain.repository.UtilityDataStoreRepository
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.wallet.WalletRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WalletModule {
    @Singleton
    @Provides
    fun provideWalletRepository(
        db: npo.kib.odc_demo.database.BlockchainDatabase,
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