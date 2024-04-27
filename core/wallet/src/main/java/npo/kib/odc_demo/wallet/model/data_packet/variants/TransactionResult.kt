package npo.kib.odc_demo.wallet.model.data_packet.variants

import kotlinx.serialization.Serializable
import npo.kib.odc_demo.wallet.model.data_packet.DataPacketType.TRANSACTION_RESULT

@Serializable
data class TransactionResult(
    val value: ResultType
) : DataPacketVariant(packetType = TRANSACTION_RESULT) {

    @Serializable
    sealed interface ResultType {
        @Serializable
        data object Success : ResultType

        @Serializable
        data class Failure(val message: String?) : ResultType
    }
}