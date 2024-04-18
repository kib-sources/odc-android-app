package npo.kib.odc_demo.feature_app.data.p2p.nfc.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object NfcModule {
    //provide NFC controller
}