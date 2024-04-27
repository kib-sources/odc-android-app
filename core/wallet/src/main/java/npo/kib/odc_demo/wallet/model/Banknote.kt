package npo.kib.odc_demo.wallet.model

import kotlinx.serialization.Serializable

@Serializable
data class Banknote(
    val bin: Int,
    val amount: Int,
    val currencyCode: Int,
    val bnid: String,
    val signature: String,
    val time: Int
)