package npo.kib.odc_demo.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.Block

@Dao
interface BlockDao {
    @Insert
    suspend fun insertBlock(block: Block)

    @Query("SELECT * FROM block WHERE block_bnid = :requiredBnid")
    suspend fun getBlocksByBnid(requiredBnid: String): List<Block>
}