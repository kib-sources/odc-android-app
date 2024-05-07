package npo.kib.odc_demo.core.wallet_repository.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import npo.kib.odc_demo.core.database.repository.BlockchainRepository
import npo.kib.odc_demo.core.datastore.DefaultDataStoreRepository
import npo.kib.odc_demo.core.datastore.UtilityDataStoreRepository
import npo.kib.odc_demo.core.network.api.BankRepository
import npo.kib.odc_demo.core.wallet_repository.repository.WalletRepository
import npo.kib.odc_demo.core.wallet_repository.repository.WalletRepositoryImpl
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object WalletRepositoryModule {
    @Singleton
    @Provides
    fun provideWalletRepository(
        blockchainRepository: BlockchainRepository,
        bankRepository: BankRepository,
        utilityDataStoreRepository: UtilityDataStoreRepository,
        defaultDataStoreRepository: DefaultDataStoreRepository
    ): WalletRepository = WalletRepositoryImpl(
        blockchainRepository = blockchainRepository,
        bankRepository = bankRepository,
        utilityDataStoreRepository = utilityDataStoreRepository,
        defaultDataStoreRepository = defaultDataStoreRepository
    )
}