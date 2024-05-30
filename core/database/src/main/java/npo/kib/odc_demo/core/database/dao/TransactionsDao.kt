package npo.kib.odc_demo.core.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import npo.kib.odc_demo.core.database.model.WalletTransactionEntity

@Dao
interface TransactionsDao {

    @Query(
        """
        SELECT * FROM wallet_transactions
        WHERE otherWid = :wid
        """
    )
    fun getTransactionsFilteredByWalletId(wid: String): Flow<List<WalletTransactionEntity>>

    @Query(
        """
        SELECT * FROM wallet_transactions
        WHERE otherName = :name
        """
    )
    fun getTransactionsFilteredByName(name: String): Flow<List<WalletTransactionEntity>>


    @Query("SELECT * FROM wallet_transactions")
    fun getAllTransactions(): Flow<List<WalletTransactionEntity>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewTransaction(transactionEntity: WalletTransactionEntity)


    @Query("""
            DELETE FROM wallet_transactions
            WHERE id = :id
        """)
    suspend fun deleteTransactionById(id: String)


    @Query("""
        DELETE FROM wallet_transactions
    """)
    suspend fun deleteAllTransactions()
}