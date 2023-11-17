package npo.kib.odc_demo.feature_app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.Block

@Dao
interface BlockDao {
    @Insert
    fun insert(block: Block)

    @Query("SELECT * FROM block WHERE block_bnid = :requiredBnid")
    suspend fun getBlocksByBnid(requiredBnid: String): List<Block>
}