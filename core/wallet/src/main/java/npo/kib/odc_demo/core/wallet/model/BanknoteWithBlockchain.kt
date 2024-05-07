package npo.kib.odc_demo.core.wallet.model

import kotlinx.serialization.Serializable
import npo.kib.odc_demo.core.wallet.model.data_packet.variants.Block

@Serializable
data class BanknoteWithBlockchain(
    val banknoteWithProtectedBlock: BanknoteWithProtectedBlock,
    val blocks: List<Block>
)