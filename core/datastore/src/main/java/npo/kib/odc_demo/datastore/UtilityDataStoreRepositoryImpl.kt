package npo.kib.odc_demo.datastore

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
import npo.kib.odc_demo.datastore.UtilityDataStoreObject.SHOULD_UPDATE_UI_USER_INFO
import npo.kib.odc_demo.feature_app.domain.repository.UtilityDataStoreRepository

class UtilityDataStoreRepositoryImpl(private val context: Context) :
    npo.kib.odc_demo.domain.UtilityDataStoreRepository {

    private companion object {
        const val NAME = "UTILITY_DATASTORE"

        fun <T> Preferences.getOrDefault(obj: UtilityDataStoreObject<T>): T =
            this[obj.key] ?: obj.defaultValue
    }

    private val datastore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(produceFile = { context.preferencesDataStoreFile(NAME) })


    override val publicUtilDataFlow: Flow<PublicUtilData> = datastore.data.map { preferences ->
        PublicUtilData(shouldUpdateUiUserInfo = preferences.getOrDefault(SHOULD_UPDATE_UI_USER_INFO))
    }.catch { exception ->
        // dataStore.data throws an IOException when an error is encountered when reading data
        if (exception is IOException) {
            emit(PublicUtilData())
        } else {
            throw exception
        }
    }

    override suspend fun <T> readValue(key: UtilityDataStoreObject<T>): T {
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

    override suspend fun <T> readValueOrDefault(key: UtilityDataStoreObject<T>): T =
        readValue(key) ?: key.defaultValue

    override suspend fun <T> writeValue(
        key: UtilityDataStoreObject<T>, value: T
    ) {
        datastore.edit { preferences ->
            preferences[key.key] = value
        }
    }

    override suspend fun clear() {
        datastore.edit { preferences -> preferences.clear() }
    }

}
