package npo.kib.odc_demo.core.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.serialization.Serializable
import npo.kib.odc_demo.data.db.BlockchainConverter

@Serializable
@Entity(tableName = "banknotes", primaryKeys = ["bnid"])
@TypeConverters(BlockchainConverter::class)
data class BanknoteWithProtectedBlock(
    @Embedded
    val banknote: Banknote,

    @Embedded
    var protectedBlock: ProtectedBlock
)