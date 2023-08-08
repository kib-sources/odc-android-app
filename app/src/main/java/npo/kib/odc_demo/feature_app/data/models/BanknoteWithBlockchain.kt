package npo.kib.odc_demo.feature_app.data.models

import kotlinx.serialization.Serializable
import npo.kib.odc_demo.common.core.models.Block
import npo.kib.odc_demo.common.core.models.BanknoteWithProtectedBlock

@Serializable
data class BanknoteWithBlockchain(
    val banknoteWithProtectedBlock: BanknoteWithProtectedBlock,
    val blocks: List<Block>
)
