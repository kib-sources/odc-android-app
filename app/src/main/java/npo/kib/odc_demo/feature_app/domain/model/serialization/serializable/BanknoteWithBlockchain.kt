package npo.kib.odc_demo.feature_app.domain.model.serialization.serializable

import kotlinx.serialization.Serializable
import npo.kib.odc_demo.common.core.models.BanknoteWithProtectedBlock
import npo.kib.odc_demo.common.core.models.Block

@Serializable
data class BanknoteWithBlockchain(
    val banknoteWithProtectedBlock: BanknoteWithProtectedBlock,
    val blocks: List<Block>
)
