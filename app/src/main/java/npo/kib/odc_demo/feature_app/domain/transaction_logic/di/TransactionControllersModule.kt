package npo.kib.odc_demo.feature_app.domain.transaction_logic.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import npo.kib.odc_demo.feature_app.di.P2PCoroutineScope
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.transaction_logic.ReceiverTransactionController
import npo.kib.odc_demo.feature_app.domain.transaction_logic.SenderTransactionController

@Module
@InstallIn(ViewModelComponent::class)
object TransactionControllersModule {


    @Provides
    @ViewModelScoped
    fun provideReceiverTransactionControllerBl(
        walletRepository: WalletRepository, @P2PCoroutineScope scope: CoroutineScope
    ) = ReceiverTransactionController(walletRepository, scope)

    @Provides
    @ViewModelScoped
    fun provideSenderTransactionController(
        walletRepository: WalletRepository, @P2PCoroutineScope scope: CoroutineScope
    ) = SenderTransactionController(walletRepository, scope)

}