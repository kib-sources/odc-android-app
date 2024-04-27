package npo.kib.odc_demo.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import npo.kib.odc_demo.database.BlockchainDatabase
import npo.kib.odc_demo.database.repository.BlockchainRepository
import npo.kib.odc_demo.database.repository.BlockchainRepositoryImpl
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    //BlockchainDatabase with BlockDao, BanknotesDao
    @Singleton
    @Provides
    fun provideBlockchainDatabase(
        @ApplicationContext
        context: Context
    ): BlockchainDatabase = Room.databaseBuilder(
        context, BlockchainDatabase::class.java, BlockchainDatabase.DATABASE_NAME
    ).build()


    //BlockchainRepository that also does conversion between models and db entities
    @Singleton
    @Provides
    fun provideBlockchainRepository(
        blockchainDb: BlockchainDatabase
    ): BlockchainRepository = BlockchainRepositoryImpl(
        blockchainDb = blockchainDb
    )

}