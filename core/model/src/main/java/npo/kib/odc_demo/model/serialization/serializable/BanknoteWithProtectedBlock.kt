package npo.kib.odc_demo.model.serialization.serializable

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.TypeConverters
import kotlinx.serialization.Serializable
import npo.kib.odc_demo.database.BlockchainConverter

@Serializable
@Entity(tableName = "banknotes", primaryKeys = ["bnid"])
@TypeConverters(BlockchainConverter::class)
data class BanknoteWithProtectedBlock(
    @Embedded
    val banknote: Banknote,
    @Embedded
    val protectedBlock: ProtectedBlock
)