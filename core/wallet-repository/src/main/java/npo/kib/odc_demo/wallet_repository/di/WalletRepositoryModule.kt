package npo.kib.odc_demo.wallet_repository.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import npo.kib.odc_demo.database.repository.BlockchainRepository
import npo.kib.odc_demo.datastore.DefaultDataStoreRepository
import npo.kib.odc_demo.datastore.UtilityDataStoreRepository
import npo.kib.odc_demo.network.api.BankRepository
import npo.kib.odc_demo.wallet_repository.repository.WalletRepository
import npo.kib.odc_demo.wallet_repository.repository.WalletRepositoryImpl
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