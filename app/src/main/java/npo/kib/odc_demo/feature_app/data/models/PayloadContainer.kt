package npo.kib.odc_demo.feature_app.data.models

import kotlinx.serialization.Serializable
import npo.kib.odc_demo.common.core.models.AcceptanceBlocks
import npo.kib.odc_demo.common.core.models.Block

@Serializable
data class PayloadContainer(
    val amountRequest: AmountRequest? = null,
    val amount: Int? = null,
    val banknoteWithBlockchain: BanknoteWithBlockchain? = null,
    val blocks: AcceptanceBlocks? = null,
    val childFull: Block? = null
)