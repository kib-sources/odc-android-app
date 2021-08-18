package npo.kib.odc_demo.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.serialization.Serializable
import npo.kib.odc_demo.data.db.BlockchainConverter

@Serializable
@Entity
@TypeConverters(BlockchainConverter::class)
data class Blockchain(
    @PrimaryKey
    val bnidKey: String,

    @Embedded
    val banknote: Banknote,

    @Embedded
    var protectedBlock: ProtectedBlock
)