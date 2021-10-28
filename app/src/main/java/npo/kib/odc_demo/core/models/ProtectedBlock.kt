package npo.kib.odc_demo.core.models

import androidx.room.ColumnInfo
import androidx.room.TypeConverters
import kotlinx.serialization.Serializable
import npo.kib.odc_demo.data.db.BlockchainConverter
import npo.kib.odc_demo.data.p2p.PublicKeySerializer
import npo.kib.odc_demo.data.p2p.UUIDSerializer
import java.security.PublicKey
import java.util.*

@Serializable
@TypeConverters(BlockchainConverter::class)
data class ProtectedBlock(
    /*
    Сопроваждающий блок для дополнительного подтверждения на Сервере.
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
