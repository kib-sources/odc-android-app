package npo.kib.odc_demo.feature_app.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.*
import npo.kib.odc_demo.feature_app.data.datastore.DefaultDataStoreKey
import npo.kib.odc_demo.feature_app.data.datastore.DefaultDataStoreKey.USER_NAME
import npo.kib.odc_demo.feature_app.domain.model.user.UserPreferences
import npo.kib.odc_demo.feature_app.domain.repository.DefaultDataStoreRepository

class DefaultDataStoreRepositoryImpl(private val context: Context) : DefaultDataStoreRepository {

    private companion object {
        const val datastore_name = "DEFAULT_DATASTORE"
    }

    private val datastore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(produceFile = { context.preferencesDataStoreFile(datastore_name) })


    override val userPreferencesFlow: Flow<UserPreferences> = datastore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            UserPreferences(userName = preferences[USER_NAME.key] ?: USER_NAME.defaultValue!! )
        }

    override suspend fun <T> readValue(key: DefaultDataStoreKey<T>): T? {
        return datastore.data.catch { exception ->
            // Handle IOException
            if (exception is IOException) {
                emit(emptyPreferences())
            }
            else {
                throw exception
            }
        }.map { preferences ->
            preferences[key.key] ?: key.defaultValue
        }.first()
    }

    override suspend fun <T> writeValue(
        key: DefaultDataStoreKey<T>,
        value: T
    ) {
        datastore.edit { preferences ->
            preferences[key.key] = value
        }
    }

    override suspend fun clear() {
        datastore.edit { preferences -> preferences.clear() }
    }

}