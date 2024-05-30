package npo.kib.odc_demo.core.model

import kotlinx.datetime.Instant

data class WalletTransaction(
    val id: Int? = null,
    val otherName : String? = null,
    val otherWid : String? = null,
    val isReceived : Boolean,
    val isWithAtm : Boolean,
    val amount : Int,
    val date : Instant
)
