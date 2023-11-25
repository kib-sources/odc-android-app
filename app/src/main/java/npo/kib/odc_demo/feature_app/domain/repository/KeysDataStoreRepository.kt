package npo.kib.odc_demo.feature_app.domain.repository

import npo.kib.odc_demo.feature_app.data.datastore.KeysDataStoreKey

interface KeysDataStoreRepository {

    suspend fun <T> readValue(key: KeysDataStoreKey<T>): T?

    suspend fun <T> writeValue(
        key: KeysDataStoreKey<T>,
        value: T
    )

    suspend fun clear()
}