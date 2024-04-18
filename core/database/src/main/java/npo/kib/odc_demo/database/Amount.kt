package npo.kib.odc_demo.database

import androidx.room.ColumnInfo

data class Amount(
    @ColumnInfo(name = "bnid")
    val bnid: String,

    @ColumnInfo(name = "amount")
    val amount: Int
)