package npo.kib.odc_demo.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import npo.kib.odc_demo.data.models.Block
import npo.kib.odc_demo.data.models.Blockchain

@Database(entities = [Blockchain::class, Block::class], version = 1)
abstract class BlockchainDatabase : RoomDatabase() {
    abstract fun blockchainDao(): BlockchainDao
    abstract fun blockDao(): BlockDao
}