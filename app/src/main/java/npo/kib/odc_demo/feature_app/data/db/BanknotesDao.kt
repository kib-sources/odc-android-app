package npo.kib.odc_demo.feature_app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithProtectedBlock

@Dao
interface BanknotesDao {
    @Insert
    fun insertBanknote(banknoteWithProtectedBlock: BanknoteWithProtectedBlock)

    @Query("SELECT bnid, amount FROM banknotes")
    suspend fun getBnidsAndAmounts(): List<Amounts>

    @Query("SELECT * FROM banknotes WHERE bnid = :requiredBnid")
    suspend fun getBanknoteByBnid(requiredBnid: String): BanknoteWithProtectedBlock

    @Query("SELECT SUM(amount) FROM banknotes")
    suspend fun getStoredSum(): Int?

    @Query("DELETE from banknotes WHERE bnid = :bnid")
    suspend fun deleteBanknoteByBnid(bnid: String)
}