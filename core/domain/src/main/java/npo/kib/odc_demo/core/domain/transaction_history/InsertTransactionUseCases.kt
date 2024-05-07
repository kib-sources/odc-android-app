package npo.kib.odc_demo.core.domain.transaction_history

import npo.kib.odc_demo.core.common_jvm.getCurrentDateTimeAsInstant
import npo.kib.odc_demo.core.database.repository.TransactionRepository
import npo.kib.odc_demo.core.model.WalletTransaction
import javax.inject.Inject

class InsertTransactionUseCases @Inject constructor(private val transactionRepository: TransactionRepository) {

    suspend fun insertNewUserP2PTransaction(
        otherName : String,
        otherWid : String,
        isReceived: Boolean,
        amount: Int
    ) =
        transactionRepository.insertNewTransaction(
            WalletTransaction(
                otherName = otherName,
                otherWid = otherWid,
                isReceived = isReceived,
                isWithAtm = false,
                amount = amount,
                date = getCurrentDateTimeAsInstant()
            )
        )

    suspend fun insertNewAtmTransaction(isReceived: Boolean = true, amount: Int) =
        transactionRepository.insertNewTransaction(
            WalletTransaction(
                isReceived = isReceived,
                isWithAtm = true,
                amount = amount,
                date = getCurrentDateTimeAsInstant()
            )
        )
}