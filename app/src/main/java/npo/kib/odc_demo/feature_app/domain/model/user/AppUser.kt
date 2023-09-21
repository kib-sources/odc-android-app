package npo.kib.odc_demo.feature_app.domain.model.user

//Need to connect to keystore or something. For log in screen work later.
data class AppUser(val name: String = "DEFAULT",
                   val imageId : Int = -1,
                   val userId : Int = -1
                   //add photo and info about logging in securely
                   )
