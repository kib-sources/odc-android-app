package npo.kib.odc_demo.feature_app.domain.repository

import kotlinx.coroutines.flow.Flow
import npo.kib.odc_demo.feature_app.data.datastore.PublicUtilData
import npo.kib.odc_demo.feature_app.data.datastore.UtilityDataStoreObject

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