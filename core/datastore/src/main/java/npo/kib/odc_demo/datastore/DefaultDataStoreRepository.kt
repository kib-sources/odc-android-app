package npo.kib.odc_demo.datastore

import kotlinx.coroutines.flow.Flow
import npo.kib.odc_demo.datastore.model.UserPreferences

interface DefaultDataStoreRepository {

    val userPreferencesFlow: Flow<UserPreferences>

    suspend fun <T> readValue(key: DefaultDataStoreObject<T>): T?

    suspend fun <T> readValueOrDefault(key: DefaultDataStoreObject<T>): T

    suspend fun <T> writeValue(
        key: DefaultDataStoreObject<T>, value: T
    )

    suspend fun clear()
}