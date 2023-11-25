package npo.kib.odc_demo.feature_app.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import npo.kib.odc_demo.feature_app.data.api.BankApi
import npo.kib.odc_demo.feature_app.data.db.BlockDao
import npo.kib.odc_demo.feature_app.data.db.BlockchainDatabase
import npo.kib.odc_demo.feature_app.data.repositories.BankRepositoryImpl
import npo.kib.odc_demo.feature_app.data.repositories.WalletRepositoryImpl
import npo.kib.odc_demo.feature_app.domain.repository.BankRepository
import npo.kib.odc_demo.feature_app.domain.repository.DefaultDataStoreRepository
import npo.kib.odc_demo.feature_app.domain.repository.KeysDataStoreRepository
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {




}