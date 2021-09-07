package npo.kib.odc_demo.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import npo.kib.odc_demo.data.models.Amounts
import npo.kib.odc_demo.data.models.Blockchain

@Dao
interface BlockchainDao {
    @Insert
    fun insertAll(blockchain: Blockchain)

    @Query("DELETE from blockchain WHERE bnidKey = :bnid")
    suspend fun delete(bnid: String)

    @Query("SELECT bnid, amount FROM blockchain")
    suspend fun getBnidsAndAmounts(): List<Amounts>

    @Query("SELECT * FROM blockchain WHERE bnid = :requiredBnid")
    suspend fun getBlockchainByBnid(requiredBnid: String): Blockchain

    @Query("SELECT SUM(amount) FROM blockchain")
    suspend fun getSum(): Int?

    @Query("SELECT SUM(amount) FROM blockchain")
    fun getSumFlow(): Flow<Int?>
}