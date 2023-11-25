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
import npo.kib.odc_demo.feature_app.data.datastore.KeysDataStoreKey
import npo.kib.odc_demo.feature_app.domain.repository.KeysDataStoreRepository

class KeysDataStoreRepositoryImpl(private val context: Context) : KeysDataStoreRepository {

    private companion object {
        const val datastore_name = "KEYS_DATASTORE"
    }

    private val datastore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(produceFile = { context.preferencesDataStoreFile(datastore_name) })


    //todo later expose a Flow containing all the datastore data converted to some data class like KeysPreferences
    /*val keysPreferencesFlow: Flow<KeysPreferences> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            // Get our show completed value, defaulting to false if not set:
            val value1 = preferences[key1]
            ...
            KeysPreferences(value1,...)
        }*/


    override suspend fun <T> readValue(key: KeysDataStoreKey<T>): T? {
        return datastore.data.catch { exception ->
            // Handle IOException
            if (exception is IOException) {
                emit(emptyPreferences())
            }
            else {
                throw exception
            }
        }.map { preferences ->
            preferences[key.it] ?: key.defaultValue
        }.first()
    }

    override suspend fun <T> writeValue(
        key: KeysDataStoreKey<T>,
        value: T
    ) {
        datastore.edit { preferences ->
            preferences[key.it] = value
        }
    }

    override suspend fun clear() {
        datastore.edit { preferences -> preferences.clear() }
    }

}
