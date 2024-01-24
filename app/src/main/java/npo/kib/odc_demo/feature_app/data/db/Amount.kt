package npo.kib.odc_demo.feature_app.data.db

import androidx.room.ColumnInfo

data class Amount(
    @ColumnInfo(name = "bnid")
    val bnid: String,

    @ColumnInfo(name = "amount")
    val amount: Int
)