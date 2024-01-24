package npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants


import kotlinx.serialization.Serializable
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.DataPacketType.USER_INFO

//todo can add photo later
@Serializable
data class UserInfo(
    val userName: String,
    val walletId: String
) : DataPacketVariant(packetType = USER_INFO)
