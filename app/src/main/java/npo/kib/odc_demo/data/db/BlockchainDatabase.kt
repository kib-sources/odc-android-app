package npo.kib.odc_demo.data.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import npo.kib.odc_demo.data.models.Block
import npo.kib.odc_demo.data.models.Blockchain

@Database(entities = [Blockchain::class, Block::class], version = 1)
abstract class BlockchainDatabase : RoomDatabase() {

    companion object {
        private const val DB_NAME = "blockchain"
        private var instance: BlockchainDatabase? = null

        @Synchronized
        fun getInstance(application: Application): BlockchainDatabase {
            if (instance == null) {
                instance =
                    Room.databaseBuilder(application, BlockchainDatabase::class.java, DB_NAME)
                        .build()
            }
            return instance as BlockchainDatabase
        }
    }

    abstract fun blockchainDao(): BlockchainDao
    abstract fun blockDao(): BlockDao
}