package npo.kib.odc_demo.common.core.models

import androidx.room.ColumnInfo
import androidx.room.TypeConverters
import kotlinx.serialization.Serializable
import npo.kib.odc_demo.feature_app.data.db.BlockchainConverter
import npo.kib.odc_demo.feature_app.data.p2p.connection_util.PublicKeySerializer
import npo.kib.odc_demo.feature_app.data.p2p.connection_util.UUIDSerializer
import java.security.PublicKey
import java.util.UUID

@Serializable
@TypeConverters(BlockchainConverter::class)
data class ProtectedBlock(
    /*
    Сопровождающий блок для дополнительного подтверждения на сервере.
    */
    @Serializable(with = PublicKeySerializer::class)
    val parentSok: PublicKey?,
    val parentSokSignature: String?,
    val parentOtokSignature: String?,

    // Ссылка на Block
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
