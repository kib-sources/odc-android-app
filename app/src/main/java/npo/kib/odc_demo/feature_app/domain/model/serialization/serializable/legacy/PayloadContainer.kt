package npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.legacy

import kotlinx.serialization.Serializable
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.AcceptanceBlocks
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.AmountRequest
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithBlockchain
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.Block

@Serializable
data class PayloadContainer(
    val amountRequest: AmountRequest? = null,
    val amount: Int? = null,
    val banknoteWithBlockchain: BanknoteWithBlockchain? = null,
    val blocks: AcceptanceBlocks? = null,
    val signedBlock: Block? = null //
)