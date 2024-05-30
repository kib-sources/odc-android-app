package npo.kib.odc_demo.core.datastore.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import npo.kib.odc_demo.core.datastore.DefaultDataStoreRepository
import npo.kib.odc_demo.core.datastore.DefaultDataStoreRepositoryImpl
import npo.kib.odc_demo.core.datastore.UtilityDataStoreRepository
import npo.kib.odc_demo.core.datastore.UtilityDataStoreRepositoryImpl
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideDefaultDataStoreRepository(
        @ApplicationContext
        context: Context
    ): DefaultDataStoreRepository = DefaultDataStoreRepositoryImpl(context = context)

    @Provides
    @Singleton
    fun provideUtilityDataStoreRepository(
        @ApplicationContext
        context: Context
    ): UtilityDataStoreRepository = UtilityDataStoreRepositoryImpl(context = context)

}