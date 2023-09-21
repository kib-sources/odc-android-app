package npo.kib.odc_demo.feature_app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import npo.kib.odc_demo.common.core.models.BanknoteWithProtectedBlock
import npo.kib.odc_demo.common.core.models.Block

@Database(
    entities = [BanknoteWithProtectedBlock::class, Block::class],
    version = 1)
abstract class BlockchainDatabase : RoomDatabase() {

    abstract val banknotesDao: BanknotesDao
    abstract val blockDao: BlockDao

    companion object {
        const val DATABASE_NAME = "blockchain"
    }

}