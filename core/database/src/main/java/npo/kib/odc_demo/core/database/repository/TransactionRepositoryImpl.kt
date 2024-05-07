package npo.kib.odc_demo.core.database.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import npo.kib.odc_demo.core.database.dao.TransactionsDao
import npo.kib.odc_demo.core.database.model.asDatabaseEntity
import npo.kib.odc_demo.core.database.model.asDomainModel
import npo.kib.odc_demo.core.model.WalletTransaction

class TransactionRepositoryImpl(private val transactionsDao: TransactionsDao) :
    TransactionRepository {

    override fun getTransactionsFilteredByWalletId(wid: String): Flow<List<WalletTransaction>> =
        transactionsDao.getTransactionsFilteredByWalletId(wid)
            .mapLatest { wteList -> wteList.map { wte -> wte.asDomainModel() } }
            .distinctUntilChanged()

    override fun getTransactionsFilteredByName(name: String): Flow<List<WalletTransaction>> =
        transactionsDao.getTransactionsFilteredByName(name)
            .mapLatest { wteList -> wteList.map { wte -> wte.asDomainModel() } }
            .distinctUntilChanged()

    override fun getAllTransactions(): Flow<List<WalletTransaction>> =
        transactionsDao.getAllTransactions().mapLatest { wteList ->
            wteList.map { wte -> wte.asDomainModel() }
        }.distinctUntilChanged()

    override suspend fun insertNewTransaction(walletTransaction: WalletTransaction) =
        transactionsDao.insertNewTransaction(walletTransaction.asDatabaseEntity())

    override suspend fun deleteTransactionById(id: String) =
        transactionsDao.deleteTransactionById(id)

    override suspend fun deleteAllTransactions() = transactionsDao.deleteAllTransactions()
}