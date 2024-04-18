package npo.kib.odc_demo.model.serialization.serializable.data_packet.variants

import npo.kib.odc_demo.model.serialization.serializable.data_packet.DataPacketType.TRANSACTION_RESULT

@Serializable
data class TransactionResult(
    val value: ResultType
) : DataPacketVariant(packetType = TRANSACTION_RESULT) {

    @kotlinx.serialization.Serializable
    sealed interface ResultType {
        @kotlinx.serialization.Serializable
        data object Success : ResultType

        @Serializable
        data class Failure(val message: String?) : ResultType
    }
}