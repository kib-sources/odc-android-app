package npo.kib.odc_demo.data.models

import kotlinx.serialization.Serializable

@Serializable
data class PayloadContainer(
    val amountRequest: AmountRequest? = null,
    val blockchain: BlockchainFromDB? = null,
    val blocks: AcceptanceBlocks? = null,
    val childFull: Block? = null
)