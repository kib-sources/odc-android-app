package npo.kib.odc_demo.core.database.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import npo.kib.odc_demo.core.database.dao.BanknotesDao
import npo.kib.odc_demo.core.database.dao.BlockDao
import npo.kib.odc_demo.core.database.dao.TransactionsDao
import npo.kib.odc_demo.core.database.repository.*
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataRepositoryModule {

    //BlockchainRepository that also does conversion between models and db entities
    @Singleton
    @Provides
    fun provideBlockchainRepository(
        blockDao: BlockDao,
        banknotesDao: BanknotesDao
    ): BlockchainRepository = BlockchainRepositoryImpl(
        blockDao = blockDao,
        banknotesDao = banknotesDao
    )


    //TransactionRepository that allows interacting with wallet transaction history.
    // Also does conversion between models and its db entities
    @Singleton
    @Provides
    fun provideTransactionRepository(
        transactionsDao: TransactionsDao
    ): TransactionRepository = TransactionRepositoryImpl(transactionsDao = transactionsDao)

}