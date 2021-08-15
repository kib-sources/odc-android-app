package npo.kib.odc_demo.data.models

import kotlinx.serialization.Serializable

@Serializable
data class PayloadContainer(
    val payloadType: Int = 0,
    val blockchainsList: ArrayList<Blockchain>? = null
)
