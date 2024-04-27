package npo.kib.odc_demo.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.TypeConverters
import npo.kib.odc_demo.database.BlockchainConverter
import npo.kib.odc_demo.wallet.model.BanknoteWithProtectedBlock

//@Serializable
@Entity(tableName = "banknotes", primaryKeys = ["bnid"])
@TypeConverters(BlockchainConverter::class)
data class BanknoteWithProtectedBlockEntity(
    @Embedded
    val banknote: BanknoteEntity,
    @Embedded
    val protectedBlock: ProtectedBlockEntity
)


fun BanknoteWithProtectedBlock.asDatabaseEntity() = BanknoteWithProtectedBlockEntity(
    banknote = banknote.asDatabaseEntity(),
    protectedBlock = protectedBlock.asDatabaseEntity()
)

fun BanknoteWithProtectedBlockEntity.asDomainModel() = BanknoteWithProtectedBlock(
    banknote = banknote.asDomainModel(),
    protectedBlock = protectedBlock.asDomainModel()
)
