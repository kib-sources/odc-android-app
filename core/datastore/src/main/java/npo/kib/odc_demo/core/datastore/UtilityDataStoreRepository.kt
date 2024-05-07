package npo.kib.odc_demo.core.datastore

import kotlinx.coroutines.flow.Flow

interface UtilityDataStoreRepository {

    val publicUtilDataFlow: Flow<PublicUtilData>

    suspend fun <T> readValue(key: UtilityDataStoreObject<T>): T?

    suspend fun <T> readValueOrDefault(key: UtilityDataStoreObject<T>): T

    suspend fun <T> writeValue(
        key: UtilityDataStoreObject<T>,
        value: T
    )

    suspend fun clear()
}