package npo.kib.odc_demo.core.wallet.model

import kotlinx.serialization.Serializable

@Serializable
data class BanknoteWithProtectedBlock(
    val banknote: Banknote,
    val protectedBlock: ProtectedBlock
)