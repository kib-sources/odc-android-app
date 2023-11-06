package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen

import npo.kib.odc_demo.feature_app.domain.model.user.AppUser

data class SendScreenState(
    var localUser : AppUser,
    var remoteUser : AppUser
)

