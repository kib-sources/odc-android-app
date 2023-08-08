package npo.kib.odc_demo.feature_app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class AmountRequest(
    val amount: Int,
    val userName: String,
    val wid: String
)