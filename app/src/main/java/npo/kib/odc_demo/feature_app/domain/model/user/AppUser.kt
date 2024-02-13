package npo.kib.odc_demo.feature_app.domain.model.user

import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.UserInfo

//Need to connect to keystore or something. For log in screen work later.
//todo
// merge with [UserInfo] ?
data class AppUser(val name: String = "DEFAULT",
                   val imageId : Int = -1,
                   val userId : Int = -1,
                   val walletId: String = "EMPTY_WID"
                   //add photo and info about logging in securely
                   ){

    fun toUserInfo() : UserInfo {
        return UserInfo(userName = this.name, walletId = walletId,
                        /* photoBytes = this.photo.toByteArray*/ )

    }

}
