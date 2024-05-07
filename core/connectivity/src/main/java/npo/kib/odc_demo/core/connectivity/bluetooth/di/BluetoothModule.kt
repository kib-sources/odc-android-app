package npo.kib.odc_demo.core.connectivity.bluetooth.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import npo.kib.odc_demo.core.connectivity.bluetooth.BluetoothController
import npo.kib.odc_demo.core.connectivity.bluetooth.BluetoothControllerImpl

@Module
@InstallIn(ViewModelComponent::class)
object BluetoothModule {
    @Provides
    @ViewModelScoped
    fun provideBluetoothController(
        @ApplicationContext context: Context
    ): BluetoothController = BluetoothControllerImpl(context)
}