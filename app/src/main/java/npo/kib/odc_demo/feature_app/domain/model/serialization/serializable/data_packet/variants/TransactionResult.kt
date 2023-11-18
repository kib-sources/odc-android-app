package npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants

import kotlinx.serialization.Serializable

@Serializable
data class TransactionResult(
    val result: ResultType
) : DataPacketVariant {
    sealed interface ResultType {
        data object Success : ResultType
        data class Failure(val message: String?) : ResultType
    }
}