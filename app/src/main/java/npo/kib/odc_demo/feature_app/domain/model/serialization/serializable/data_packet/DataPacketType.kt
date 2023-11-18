package npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet

import kotlinx.serialization.Serializable
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.DataPacketVariant

/**
 * Each type corresponds to a serializable data class that implements [DataPacketVariant]
 * */
@Serializable
enum class DataPacketType {
    USER_INFO,
    AMOUNT_REQUEST,
    BANKNOTES_LIST,
    SIGNED_BLOCK,
    ACCEPTANCE_BLOCKS,
    RESULT
}
