package npo.kib.odc_demo.feature_app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.*
import javax.inject.Qualifier


@Module
@InstallIn(ViewModelComponent::class)
object CoroutinesModule {

    @Provides
    @P2PUseCaseScope
    @ViewModelScoped
    fun provideP2PCoroutineScope() = CoroutineScope(Job() + Dispatchers.Default)

    @Provides
    @P2PTransactionScope
    @ViewModelScoped
    fun provideP2PTransactionScope() = CoroutineScope(Job() + Dispatchers.Default)

//    @Provides
//    @P2PConnectionScope
//    @ViewModelScoped
//    fun provideP2PConnectionScope() = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}

@Qualifier
annotation class P2PUseCaseScope

@Qualifier
annotation class P2PTransactionScope

//@Qualifier
//annotation class P2PConnectionScope