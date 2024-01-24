package npo.kib.odc_demo.feature_app.data.p2p.nfc.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import npo.kib.odc_demo.feature_app.data.p2p.nfc.P2PConnectionNfcImpl
import npo.kib.odc_demo.feature_app.di.NfcP2PConnection
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnection
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NfcModule {

    @Provides
    @Singleton
    @NfcP2PConnection
    fun provideP2PConnectionNfcImpl(@ApplicationContext context: Context) =
        P2PConnectionNfcImpl(context) as P2PConnection


}