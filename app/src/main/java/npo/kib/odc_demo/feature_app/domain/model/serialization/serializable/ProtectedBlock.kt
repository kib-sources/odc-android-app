package npo.kib.odc_demo.feature_app.domain.model.serialization.serializable

import androidx.room.ColumnInfo
import androidx.room.TypeConverters
import kotlinx.serialization.Serializable
import npo.kib.odc_demo.feature_app.data.db.BlockchainConverter
import npo.kib.odc_demo.feature_app.domain.model.serialization.PublicKeySerializer
import npo.kib.odc_demo.feature_app.domain.model.serialization.UUIDSerializer
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.Block
import java.security.PublicKey
import java.util.UUID

/**
 * Accompanying block for additional verification on server
 * @param refUuid Link to a [Block]
 */
@Serializable
@TypeConverters(BlockchainConverter::class)
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

    @ColumnInfo(name = "protected_time")
    val time: Int
)
