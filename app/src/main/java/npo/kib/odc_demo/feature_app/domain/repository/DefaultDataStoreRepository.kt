package npo.kib.odc_demo.feature_app.domain.repository

import kotlinx.coroutines.flow.Flow
import npo.kib.odc_demo.feature_app.data.datastore.DefaultDataStoreKey
import npo.kib.odc_demo.feature_app.domain.model.user.UserPreferences

interface DefaultDataStoreRepository {

    val userPreferencesFlow: Flow<UserPreferences>
    suspend fun <T> readValue(key: DefaultDataStoreKey<T>): T?

    suspend fun <T> writeValue(
        key: DefaultDataStoreKey<T>,
        value: T
    )

    suspend fun clear()
}