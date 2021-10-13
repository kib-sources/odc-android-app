package npo.kib.odc_demo.core.models

import kotlinx.serialization.Serializable

@Serializable
data class AcceptanceBlocks(
    val childBlock: Block,
    val protectedBlock: ProtectedBlock
)