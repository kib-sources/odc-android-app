package npo.kib.odc_demo.core.database.repository

import kotlinx.coroutines.flow.Flow
import npo.kib.odc_demo.core.database.BlockchainDatabase
import npo.kib.odc_demo.core.model.WalletTransaction

/**
 *  Used to access transaction history table in [BlockchainDatabase]
 * */
interface TransactionRepository {

    fun getTransactionsFilteredByWalletId(wid: String): Flow<List<WalletTransaction>>

    fun getTransactionsFilteredByName(name: String): Flow<List<WalletTransaction>>

    fun getAllTransactions(): Flow<List<WalletTransaction>>

    suspend fun insertNewTransaction(walletTransaction: WalletTransaction)

    suspend fun deleteTransactionById(id: String)

    suspend fun deleteAllTransactions()
}