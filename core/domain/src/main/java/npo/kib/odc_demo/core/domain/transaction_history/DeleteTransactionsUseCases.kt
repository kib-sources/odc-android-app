package npo.kib.odc_demo.core.domain.transaction_history

import npo.kib.odc_demo.core.database.repository.TransactionRepository
import javax.inject.Inject

class DeleteTransactionsUseCases @Inject constructor(private val transactionRepository: TransactionRepository) {

    suspend fun deleteTransactionById(id: String) = transactionRepository.deleteTransactionById(id)

    suspend fun deleteAllTransactions() = transactionRepository.deleteAllTransactions()
}