package npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants

import kotlinx.serialization.Serializable


/**
 * @param walletId only for user identification in ui, not needed since [UserInfo] is available
 * */
@Serializable
data class AmountRequest(
    val amount: Int,
    val walletId: String
) : DataPacketVariant