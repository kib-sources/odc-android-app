package npo.kib.odc_demo.common.core.models

import kotlinx.serialization.Serializable
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.Block

@Serializable
data class AcceptanceBlocks(
    val childBlock: Block,
    val protectedBlock: ProtectedBlock
)