package npo.kib.odc_demo.feature_app.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import npo.kib.odc_demo.feature_app.data.datastore.DefaultDataStoreKey
import npo.kib.odc_demo.feature_app.domain.repository.DefaultDataStoreRepository

class DefaultDataStoreRepositoryImpl(private val context: Context) : DefaultDataStoreRepository {

    private companion object {
        const val datastore_name = "DEFAULT_DATASTORE"
    }

    private val datastore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(produceFile = { context.preferencesDataStoreFile(datastore_name) })


    //todo later expose a Flow containing all the datastore data converted to some data class like UserData
    /*val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            // Get our show completed value, defaulting to false if not set:
            val userName = preferences[username key]?: default username
            ...
            UserPreferences(userName,...)
        }*/

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