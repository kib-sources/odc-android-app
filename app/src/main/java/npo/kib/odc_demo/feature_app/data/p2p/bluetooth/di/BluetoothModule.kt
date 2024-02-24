package npo.kib.odc_demo.feature_app.data.p2p.bluetooth.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import npo.kib.odc_demo.feature_app.data.p2p.bluetooth.BluetoothControllerImpl
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.BluetoothController

@Module
@InstallIn(ViewModelComponent::class)
object BluetoothModule {
    @Provides
    @ViewModelScoped
    fun provideBluetoothController(
        @ApplicationContext context: Context
    ): BluetoothController = BluetoothControllerImpl(context)
}