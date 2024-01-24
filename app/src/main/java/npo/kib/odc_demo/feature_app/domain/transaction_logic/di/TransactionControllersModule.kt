package npo.kib.odc_demo.feature_app.domain.transaction_logic.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.transaction_logic.ReceiverTransactionController
import npo.kib.odc_demo.feature_app.domain.transaction_logic.SenderTransactionController
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TransactionControllersModule {

    @Singleton
    @Provides
    fun provideReceiverTransactionControllerBl(
        walletRepository: WalletRepository
    ) = ReceiverTransactionController(walletRepository)

    @Singleton
    @Provides
    fun provideSenderTransactionController(
        walletRepository: WalletRepository
    ) = SenderTransactionController(walletRepository)

}