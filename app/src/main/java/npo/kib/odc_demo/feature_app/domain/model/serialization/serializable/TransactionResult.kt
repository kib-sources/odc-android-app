package npo.kib.odc_demo.feature_app.domain.model.serialization.serializable

import kotlinx.serialization.Serializable

@Serializable
data class TransactionResult(
    val result: ResultType
) : DataPacketTypeMarker

sealed interface ResultType {
    data object Success : ResultType
    data class Failure(val message: String?) : ResultType
}
