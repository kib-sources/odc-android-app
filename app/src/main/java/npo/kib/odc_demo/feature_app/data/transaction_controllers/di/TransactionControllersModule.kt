package npo.kib.odc_demo.feature_app.data.transaction_controllers.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import npo.kib.odc_demo.feature_app.data.transaction_controllers.ReceiverTransactionControllerBlImpl
import npo.kib.odc_demo.feature_app.data.transaction_controllers.SenderTransactionControllerBlImpl
import npo.kib.odc_demo.feature_app.di.BluetoothP2PConnection
import npo.kib.odc_demo.feature_app.di.ReceiverControllerBluetooth
import npo.kib.odc_demo.feature_app.di.SenderControllerBluetooth
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnection
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.transaction_logic.ReceiverTransactionController
import npo.kib.odc_demo.feature_app.domain.transaction_logic.SenderTransactionController
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object TransactionControllersModule {

    @Singleton
    @Provides
    @ReceiverControllerBluetooth
    fun provideReceiverTransactionControllerBl(
        @BluetoothP2PConnection p2PConnection: P2PConnection, walletRepository: WalletRepository
    ): ReceiverTransactionController =
        ReceiverTransactionControllerBlImpl(p2PConnection, walletRepository)

    @Singleton
    @Provides
    @SenderControllerBluetooth
    fun provideSenderTransactionControllerBl(
        @BluetoothP2PConnection p2PConnection: P2PConnection, walletRepository: WalletRepository
    ): SenderTransactionController = SenderTransactionControllerBlImpl(p2PConnection, walletRepository)

}