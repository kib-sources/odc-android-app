package npo.kib.odc_demo.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import npo.kib.odc_demo.database.model.AmountEntity
import npo.kib.odc_demo.database.model.BanknoteWithProtectedBlockEntity

@Dao
interface BanknotesDao {
    @Insert
    suspend fun insertBanknote(banknoteWithProtectedBlockEntity: BanknoteWithProtectedBlockEntity)

    @Query("SELECT bnid, amount FROM banknotes")
    suspend fun getBnidsAndAmounts(): List<AmountEntity>

    @Query("SELECT * FROM banknotes WHERE bnid = :requiredBnid")
    suspend fun getBanknoteByBnid(requiredBnid: String): BanknoteWithProtectedBlockEntity

    @Query("SELECT SUM(amount) FROM banknotes")
    suspend fun getStoredSum(): Int?

    @Query("DELETE from banknotes WHERE bnid = :bnid")
    suspend fun deleteBanknoteByBnid(bnid: String)
}