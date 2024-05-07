package npo.kib.odc_demo.core.domain.transaction_history

import npo.kib.odc_demo.core.database.repository.TransactionRepository
import javax.inject.Inject

class GetTransactionsUseCases @Inject constructor(private val transactionRepository: TransactionRepository) {

    fun getAllTransactions() = transactionRepository.getAllTransactions()

    fun getTransactionsFilteredByName(name: String) =
        transactionRepository.getTransactionsFilteredByName(name)

    fun getTransactionsFilteredByWalletId(wid: String) =
        transactionRepository.getTransactionsFilteredByWalletId(wid)

}