package npo.kib.odc_demo.feature_app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnection
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.P2PConnectionBluetooth
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PBaseUseCase
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

//Don't need "includes=" here because the AppModule is already added to the
//same SingletonComponent and, thus, to Hilt's object graph
@Module(/*includes = [AppModule::class]*/)
@InstallIn(SingletonComponent::class)
object UseCasesModule {

    @Provides
    @Singleton
    @SendUseCase
    fun provideSendUseCase(
        walletRepository: WalletRepository,
        p2p: P2PConnectionBluetooth
    ): P2PBaseUseCase = P2PSendUseCase(walletRepository = walletRepository, p2pConnection = p2p)

    @Provides
    @Singleton
    @ReceiveUseCase
    fun provideReceiveUseCase(
        walletRepository: WalletRepository, p2p: P2PConnectionBluetooth
    ): P2PBaseUseCase = P2PReceiveUseCase(walletRepository = walletRepository, p2pConnection = p2p)

    @Provides
    @Singleton
    @AtmUseCase
    fun provideAtmUseCase(
        walletRepository: WalletRepository, @NfcP2PConnection p2p: P2PConnection
    ): P2PBaseUseCase = P2PReceiveUseCase(walletRepository = walletRepository, p2pConnection = p2p)

}