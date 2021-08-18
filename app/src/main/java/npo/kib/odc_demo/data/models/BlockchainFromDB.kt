package npo.kib.odc_demo.data.models

import kotlinx.serialization.Serializable
import npo.kib.odc_demo.data.models.Block
import npo.kib.odc_demo.data.models.Blockchain

@Serializable
data class BlockchainFromDB(
    val blockchain: Blockchain,
    val blocks: List<Block>
)