package npo.kib.odc_demo.core.connectivity.nfc.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import npo.kib.odc_demo.core.connectivity.bluetooth.BluetoothController
import npo.kib.odc_demo.core.connectivity.bluetooth.BluetoothControllerImpl
import npo.kib.odc_demo.core.connectivity.nfc.NfcController
import npo.kib.odc_demo.core.connectivity.nfc.NfcControllerImpl

@Module
@InstallIn(ViewModelComponent::class)
object NfcModule {
    @Provides
    @ViewModelScoped
    fun provideNfcController(
        @ApplicationContext context: Context
    ): NfcController = NfcControllerImpl(context)
}