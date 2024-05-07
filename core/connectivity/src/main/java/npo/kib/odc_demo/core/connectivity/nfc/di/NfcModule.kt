package npo.kib.odc_demo.core.connectivity.nfc.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object NfcModule {
    //provide NFC controller
}