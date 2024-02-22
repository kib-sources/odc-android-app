package npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants

import kotlinx.serialization.Serializable
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.DataPacketType.TRANSACTION_RESULT

@Serializable
data class TransactionResult(
    val value: ResultType
) : DataPacketVariant(packetType = TRANSACTION_RESULT) {
    sealed interface ResultType {
        data object Success : ResultType
        data class Failure(val message: String?) : ResultType
    }
}