package npo.kib.odc_demo.core.database.model

import npo.kib.odc_demo.core.wallet.model.Banknote

data class BanknoteEntity(
    val bin: Int,
    val amount: Int,
    val currencyCode: Int,
    val bnid: String,
    val signature: String,
    val time: Int
)


fun Banknote.asDatabaseEntity() = BanknoteEntity(
    bin = bin,
    amount = amount,
    currencyCode = currencyCode,
    bnid = bnid,
    signature = signature,
    time = time
)


fun BanknoteEntity.asDomainModel() = Banknote(
    bin = bin,
    amount = amount,
    currencyCode = currencyCode,
    bnid = bnid,
    signature = signature,
    time = time
)