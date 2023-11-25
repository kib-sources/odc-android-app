package npo.kib.odc_demo.feature_app.domain.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import npo.kib.odc_demo.feature_app.data.datastore.DataStoreKey
import npo.kib.odc_demo.feature_app.data.datastore.DefaultDataStoreKey

interface DefaultDataStoreRepository {

    suspend fun <T> readValue(key: DefaultDataStoreKey<T>): T?

    suspend fun <T> writeValue(
        key: DefaultDataStoreKey<T>,
        value: T
    )

    suspend fun clear()
}