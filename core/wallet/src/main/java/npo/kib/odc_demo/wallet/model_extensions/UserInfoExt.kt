package npo.kib.odc_demo.wallet.model_extensions

import npo.kib.odc_demo.model.user.AppUser
import npo.kib.odc_demo.wallet.model.data_packet.variants.UserInfo

fun AppUser.asUserInfo() = UserInfo(userName = userName, walletId = walletId)

fun UserInfo.asAppUser() = AppUser(userName, walletId)