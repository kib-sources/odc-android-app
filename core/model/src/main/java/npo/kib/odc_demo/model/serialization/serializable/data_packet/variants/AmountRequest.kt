package npo.kib.odc_demo.model.serialization.serializable.data_packet.variants

import npo.kib.odc_demo.model.serialization.serializable.data_packet.DataPacketType.AMOUNT_REQUEST


/**
 * @param walletId only for user identification, not needed since [UserInfo] is available
 * */
@Serializable
data class AmountRequest(
    val amount: Int,
    val walletId: String
) : DataPacketVariant(packetType = AMOUNT_REQUEST)