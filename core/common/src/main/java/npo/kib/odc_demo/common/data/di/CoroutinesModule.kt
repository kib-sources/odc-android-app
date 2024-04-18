package npo.kib.odc_demo.feature_app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

@Module
@InstallIn(ViewModelComponent::class)
object CoroutinesModule {

    @Provides
    @P2PUseCaseScope
    @ViewModelScoped
    fun provideP2PCoroutineScope() = CoroutineScope(Job() + Dispatchers.Default)
}