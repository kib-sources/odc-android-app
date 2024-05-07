package npo.kib.odc_demo.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import npo.kib.odc_demo.core.model.WalletTransaction
import kotlinx.datetime.Instant

/**
 * Defines a table where past wallet transactions made with this device are stored.
 *  Includes both top-ups and deposits with ATM and p2p exchanges.
 * */
@Entity(tableName = "wallet_transactions")
data class WalletTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    @ColumnInfo(name = "otherName")
    val otherName : String?, // name of an ATM currently will be null
    @ColumnInfo(name = "otherWid")
    val otherWid : String?, // wid of an ATM currently will be null
    val isReceived : Boolean,
    val isWithAtm : Boolean,
    val amount : Int,
    @ColumnInfo(name = "date_time")
    val date : Instant
)


fun WalletTransactionEntity.asDomainModel() = WalletTransaction(
    id = id,
    otherName = otherName,
    otherWid = otherWid,
    isReceived = isReceived,
    isWithAtm = isWithAtm,
    amount = amount,
    date = date
)


fun WalletTransaction.asDatabaseEntity() = WalletTransactionEntity(
    id = id,
    otherName = otherName,
    otherWid = otherWid,
    isReceived = isReceived,
    isWithAtm = isWithAtm,
    amount = amount,
    date = date
)