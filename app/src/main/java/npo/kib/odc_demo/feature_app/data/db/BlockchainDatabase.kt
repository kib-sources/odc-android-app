package npo.kib.odc_demo.feature_app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import npo.kib.odc_demo.common.core.models.Block
import npo.kib.odc_demo.common.core.models.BanknoteWithProtectedBlock

@Database(entities = [BanknoteWithProtectedBlock::class, Block::class], version = 1)
abstract class BlockchainDatabase : RoomDatabase() {

    companion object {
        private const val DB_NAME = "blockchain"
        private var instance: BlockchainDatabase? = null

        @Synchronized //replace Context with Application
        fun getInstance(context: Context): BlockchainDatabase {
            if (instance == null) {
                instance =
                    Room.databaseBuilder(context, BlockchainDatabase::class.java, DB_NAME).build()
            }
            return instance as BlockchainDatabase
        }
    }

    abstract fun banknotesDao(): BanknotesDao
    abstract fun blockDao(): BlockDao
}