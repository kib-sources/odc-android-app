package npo.kib.odc_demo.database

import androidx.room.Database
import androidx.room.RoomDatabase
import npo.kib.odc_demo.database.dao.BanknotesDao
import npo.kib.odc_demo.database.dao.BlockDao
import npo.kib.odc_demo.database.model.BanknoteWithProtectedBlockEntity
import npo.kib.odc_demo.database.model.BlockEntity

@Database(
    entities = [BanknoteWithProtectedBlockEntity::class, BlockEntity::class],
    version = 2)
abstract class BlockchainDatabase : RoomDatabase() {

    abstract val banknotesDao: BanknotesDao
    abstract val blockDao: BlockDao

    companion object {
        const val DATABASE_NAME = "blockchain"
    }

}