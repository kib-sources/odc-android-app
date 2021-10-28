package npo.kib.odc_demo.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import npo.kib.odc_demo.data.models.Amounts
import npo.kib.odc_demo.core.models.BanknoteWithProtectedBlock

@Dao
interface BanknotesDao {
    @Insert
    fun insert(banknoteWithProtectedBlock: BanknoteWithProtectedBlock)

    @Query("SELECT bnid, amount FROM banknotes")
    suspend fun getBnidsAndAmounts(): List<Amounts>

    @Query("SELECT * FROM banknotes WHERE bnid = :requiredBnid")
    suspend fun getBlockchainByBnid(requiredBnid: String): BanknoteWithProtectedBlock

    @Query("SELECT SUM(amount) FROM banknotes")
    suspend fun getStoredSum(): Int?

    @Query("SELECT SUM(amount) FROM banknotes")
    fun getStoredSumAsFlow(): Flow<Int?>

    @Query("DELETE from banknotes WHERE bnid = :bnid")
    suspend fun deleteByBnid(bnid: String)
}