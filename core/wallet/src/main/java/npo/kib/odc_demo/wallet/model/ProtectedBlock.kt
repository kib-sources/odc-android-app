package npo.kib.odc_demo.wallet.model

import kotlinx.serialization.Serializable
import npo.kib.odc_demo.wallet.model.serialization.PublicKeySerializer
import npo.kib.odc_demo.wallet.model.serialization.UUIDSerializer
import java.security.PublicKey
import java.util.UUID

@Serializable
data class ProtectedBlock(
    @Serializable(with = PublicKeySerializer::class)
    val parentSok: PublicKey?,
    val parentSokSignature: String?,
    val parentOtokSignature: String?,

    @Serializable(with = UUIDSerializer::class)
    val refUuid: UUID?,

    @Serializable(with = PublicKeySerializer::class)
    val sok: PublicKey?,
    val sokSignature: String?,
    val otokSignature: String,
    val transactionSignature: String,

    val time: Int
)