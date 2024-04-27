package npo.kib.odc_demo.datastore.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import npo.kib.odc_demo.datastore.DefaultDataStoreRepository
import npo.kib.odc_demo.datastore.DefaultDataStoreRepositoryImpl
import npo.kib.odc_demo.datastore.UtilityDataStoreRepository
import npo.kib.odc_demo.datastore.UtilityDataStoreRepositoryImpl
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