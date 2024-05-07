package npo.kib.odc_demo.core.wallet.model.data_packet.variants

import kotlinx.serialization.Serializable
import npo.kib.odc_demo.core.wallet.model.ProtectedBlock
import npo.kib.odc_demo.core.wallet.model.data_packet.DataPacketType.ACCEPTANCE_BLOCKS


/**
 * Sent on step 4, acceptance_init
 * @param childBlock Unsigned [Block]
 * @param protectedBlock Accompanying block for additional verification on server
 * */
@Serializable
data class AcceptanceBlocks(
    val childBlock: Block,
    val protectedBlock: ProtectedBlock
) : DataPacketVariant(packetType = ACCEPTANCE_BLOCKS)