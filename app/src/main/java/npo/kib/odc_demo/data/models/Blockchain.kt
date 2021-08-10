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
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    @Embedded
    val banknote: Banknote,
    @Embedded(prefix = "block_")
    val block: Block,
    @Embedded(prefix = "protected_block_")
    val protectedBlock: ProtectedBlock
)
