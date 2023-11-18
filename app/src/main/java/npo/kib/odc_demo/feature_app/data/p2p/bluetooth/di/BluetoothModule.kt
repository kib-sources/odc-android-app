package npo.kib.odc_demo.feature_app.data.p2p.bluetooth.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import npo.kib.odc_demo.feature_app.data.p2p.bluetooth.BluetoothControllerImpl
import npo.kib.odc_demo.feature_app.data.p2p.bluetooth.P2PConnectionBluetoothImpl
import npo.kib.odc_demo.feature_app.di.BluetoothP2PConnection
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnection
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.BluetoothController
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.P2PConnectionBluetooth
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {

    @Provides
    @Singleton
    fun provideBluetoothController(@ApplicationContext context: Context): BluetoothController =
        BluetoothControllerImpl(context)

    @Provides
    @Singleton
    @BluetoothP2PConnection
    fun provideP2PConnectionBluetoothImpl(
        controller: BluetoothController, @ApplicationContext context: Context
    ) = P2PConnectionBluetoothImpl(
        bluetoothController = controller, context = context
    ) as P2PConnection

    @Provides
    @Singleton
    @BluetoothP2PConnection
    fun provideP2PConnectionBidirectionalBluetoothImpl(
        controller: BluetoothController, @ApplicationContext context: Context
    ) = P2PConnectionBluetoothImpl(
        bluetoothController = controller, context = context
    ) as P2PConnectionBluetooth




}