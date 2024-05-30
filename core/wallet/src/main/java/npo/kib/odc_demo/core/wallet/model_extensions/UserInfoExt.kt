package npo.kib.odc_demo.core.wallet.model_extensions

import npo.kib.odc_demo.core.model.user.AppUser
import npo.kib.odc_demo.core.wallet.model.data_packet.variants.UserInfo

fun AppUser.asUserInfo() = UserInfo(userName = userName, walletId = walletId)

fun UserInfo.asAppUser() = AppUser(userName, walletId)