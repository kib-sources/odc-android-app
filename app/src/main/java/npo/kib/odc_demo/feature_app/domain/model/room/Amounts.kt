package npo.kib.odc_demo.feature_app.domain.model.room

import androidx.room.ColumnInfo

data class Amounts(
    @ColumnInfo(name = "bnid")
    val bnid: String,

    @ColumnInfo(name = "amount")
    val amount: Int
)