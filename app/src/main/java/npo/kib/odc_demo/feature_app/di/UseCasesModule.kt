package npo.kib.odc_demo.feature_app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.BluetoothController
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.transaction_logic.ReceiverTransactionController
import npo.kib.odc_demo.feature_app.domain.transaction_logic.SenderTransactionController
import npo.kib.odc_demo.feature_app.domain.use_cases.GetInfoFromWalletUseCase
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PReceiveUseCase
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PSendUseCase
import javax.inject.Singleton


//https://developer.android.com/training/dependency-injection/dagger-android

//Once a module has been added to
// -either a component or
// -another module,
// it's already in the Dagger graph;
// Dagger can provide those objects in that component.
//
// Before adding a module, check if that module is part
// of the Dagger graph already by checking if it's already
// added to the component or by compiling the project
// and seeing if Dagger can find the required dependencies for that module.
//
//Good practice dictates that modules should only be declared once in a component
// (outside of specific advanced Dagger use cases).

//Don't need "includes = [AppModule::class]" here because the AppModule is already added to the
//same SingletonComponent and, thus, to Hilt's object graph
@Module
@InstallIn(ViewModelComponent::class)
object UseCasesModule {
    @Provides
    @ViewModelScoped
    fun provideReceiveUseCase(
        transactionController: ReceiverTransactionController,
        bluetoothController: BluetoothController,
        @P2PUseCaseScope scope: CoroutineScope
    ) = P2PReceiveUseCase(transactionController, bluetoothController, scope)

    @Provides
    @ViewModelScoped
    fun provideSendUseCase(
        transactionController: SenderTransactionController,
        bluetoothController: BluetoothController,
        @P2PUseCaseScope scope: CoroutineScope
    ) = P2PSendUseCase(transactionController, bluetoothController, scope)

    @Provides
    @ViewModelScoped
    fun provideGetInfoFromWalletUseCase(walletRepository: WalletRepository) = GetInfoFromWalletUseCase(walletRepository)
}