package npo.kib.odc_demo.core.wallet.model

import kotlinx.serialization.Serializable

@Serializable
data class Amount(
    val bnid: String,
    val amount: Int
)