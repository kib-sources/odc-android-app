package npo.kib.odc_demo.model.user

import npo.kib.odc_demo.model.serialization.serializable.data_packet.variants.UserInfo

//Need to connect to keystore or something. For log in screen work later.
data class AppUser(
    val userName: String = "Default user",
    val walletId: String = "EMPTY_WID"
    //add photo and info about logging in securely
//    val imageId: Int = -1,
//    val userId: Int = -1,
) {
    fun toUserInfo(): UserInfo = UserInfo(
        userName = this.userName,
        walletId = walletId,
        /* photoBytes = this.getStoredImage(imageId).toByteArray*/
    )
}

fun UserInfo.toAppUser(): AppUser = AppUser(
    userName = this.userName,
    walletId = this.walletId
)