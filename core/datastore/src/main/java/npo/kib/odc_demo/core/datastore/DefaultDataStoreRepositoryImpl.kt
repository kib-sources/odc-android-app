package npo.kib.odc_demo.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import npo.kib.odc_demo.core.datastore.DefaultDataStoreObject.USER_NAME
import npo.kib.odc_demo.core.datastore.model.UserPreferences

class DefaultDataStoreRepositoryImpl(private val context: Context) :
    DefaultDataStoreRepository {

    private companion object {
        const val NAME = "DEFAULT_DATASTORE"

        fun <T> Preferences.getOrDefault(obj: DefaultDataStoreObject<T>): T =
            this[obj.key] ?: obj.defaultValue
    }

    private val datastore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(produceFile = { context.preferencesDataStoreFile(NAME) })


    override val userPreferencesFlow: Flow<UserPreferences> = datastore.data.map { preferences ->
        UserPreferences(userName = preferences.getOrDefault(USER_NAME))
    }.catch { exception ->
        // dataStore.data throws an IOException when an error is encountered when reading data
        if (exception is IOException) {
            emit(UserPreferences())
        } else {
            throw exception
        }
    }

    override suspend fun <T> readValue(key: DefaultDataStoreObject<T>): T? {
        return datastore.data.catch { exception ->
            // Handle IOException
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[key.key]
        }.first()
    }

    override suspend fun <T> readValueOrDefault(key: DefaultDataStoreObject<T>): T =
        readValue(key) ?: key.defaultValue

    override suspend fun <T> writeValue(
        key: DefaultDataStoreObject<T>, value: T
    ) {
        datastore.edit { preferences ->
            preferences[key.key] = value
        }
    }
//todo can save UserPreferences as a parcelable data structure, add it to keys ?

    override suspend fun clear() {
        datastore.edit { preferences -> preferences.clear() }
    }
}