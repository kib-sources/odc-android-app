package npo.kib.odc_demo.database.model

import androidx.room.ColumnInfo
import npo.kib.odc_demo.wallet.model.Amount

data class AmountEntity(
    @ColumnInfo(name = "bnid")
    val bnid: String,

    @ColumnInfo(name = "amount")
    val amount: Int
)

fun AmountEntity.asDomainModel() = Amount(bnid, amount)
fun Amount.asDatabaseEntity() = AmountEntity(bnid, amount)