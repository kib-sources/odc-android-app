package npo.kib.odc_demo.feature_app.domain.repository

import npo.kib.odc_demo.feature_app.data.datastore.UtilityDataStoreKey

interface UtilityDataStoreRepository {

    suspend fun <T> readValue(key: UtilityDataStoreKey<T>): T?

    suspend fun <T> writeValue(
        key: UtilityDataStoreKey<T>,
        value: T
    )

    suspend fun clear()
}