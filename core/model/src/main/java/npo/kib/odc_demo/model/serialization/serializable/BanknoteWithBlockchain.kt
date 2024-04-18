package npo.kib.odc_demo.model.serialization.serializable

import kotlinx.serialization.Serializable
import npo.kib.odc_demo.model.serialization.serializable.data_packet.variants.Block

@Serializable
data class BanknoteWithBlockchain(
    val banknoteWithProtectedBlock: BanknoteWithProtectedBlock,
    val blocks: List<Block>
)