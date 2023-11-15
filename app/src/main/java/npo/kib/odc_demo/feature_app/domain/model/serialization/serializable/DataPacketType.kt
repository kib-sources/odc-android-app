package npo.kib.odc_demo.feature_app.domain.model.serialization.serializable

import kotlinx.serialization.Serializable


@Serializable
enum class DataPacketType {
    USER_INFO,
    AMOUNT,
    BANKNOTES,
    UNSIGNED_BLOCK,
    SIGNED_BLOCK,
    RESULT
}
