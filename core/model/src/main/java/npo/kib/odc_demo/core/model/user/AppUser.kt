package npo.kib.odc_demo.core.model.user

//Need to connect to keystore or something. For log in screen work later.
data class AppUser(
    val userName: String = "Default user",
    val walletId: String = "EMPTY_WID"
    //add photo and info about logging in securely
//    val imageId: Int = -1,
//    val userId: Int = -1,
)
