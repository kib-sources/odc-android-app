package npo.kib.odc_demo.feature_app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(ViewModelComponent::class)
object CoroutinesModule {

    @Provides
    @P2PCoroutineScope
    @ViewModelScoped
    fun provideP2PCoroutineScope() = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)


    @Provides
    @P2PConnectionScope
    @ViewModelScoped
    fun provideP2PConnectionScope() = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    @Provides
    @P2PTransactionScope
    @ViewModelScoped
    fun provideP2PTransactionScope() = CoroutineScope(SupervisorJob() + Dispatchers.Default)

}


@Qualifier
annotation class P2PCoroutineScope

@Qualifier
annotation class P2PConnectionScope

@Qualifier
annotation class P2PTransactionScope