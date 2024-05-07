package npo.kib.odc_demo.core.transaction_logic.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object TransactionControllersModule {

//    @Provides
//    @ViewModelScoped
//    fun provideReceiverTransactionControllerBl(
//        walletRepository: WalletRepository, @P2PTransactionScope scope: CoroutineScope
//    ) = ReceiverTransactionController(walletRepository, scope)
//
//    @Provides
//    @ViewModelScoped
//    fun provideSenderTransactionController(
//        walletRepository: WalletRepository, @P2PTransactionScope scope: CoroutineScope
//    ) = SenderTransactionController(walletRepository, scope)

}