package npo.kib.odc_demo.feature_app.domain.model.serialization.serializable


import kotlinx.serialization.Serializable

//todo can add photo later
@Serializable
data class UserInfo(
    val userName: String,
    val walletId: String
) : DataPacketTypeMarker
