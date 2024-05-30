package npo.kib.odc_demo.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import npo.kib.odc_demo.core.database.model.BlockEntity

@Dao
interface BlockDao {
    @Insert
    suspend fun insertBlock(blockEntity: BlockEntity)

    @Query("SELECT * FROM block WHERE block_bnid = :requiredBnid")
    suspend fun getBlocksByBnid(requiredBnid: String): List<BlockEntity>
}