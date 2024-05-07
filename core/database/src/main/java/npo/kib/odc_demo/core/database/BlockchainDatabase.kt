package npo.kib.odc_demo.core.database

import androidx.room.*
import npo.kib.odc_demo.core.database.dao.BanknotesDao
import npo.kib.odc_demo.core.database.dao.BlockDao
import npo.kib.odc_demo.core.database.dao.TransactionsDao
import npo.kib.odc_demo.core.database.model.BanknoteWithProtectedBlockEntity
import npo.kib.odc_demo.core.database.model.BlockEntity
import npo.kib.odc_demo.core.database.model.WalletTransactionEntity
import npo.kib.odc_demo.core.database.util.InstantConverter

@Database(
    entities = [BanknoteWithProtectedBlockEntity::class, BlockEntity::class, WalletTransactionEntity::class],
    version = BlockchainDatabase.LATEST_VERSION,
    autoMigrations = [AutoMigration(from = 1, to = 2), AutoMigration(from = 2, to = 3)],
    exportSchema = true
)
@TypeConverters(InstantConverter::class)
internal abstract class BlockchainDatabase : RoomDatabase() {
    internal companion object {
        const val LATEST_VERSION = 3
        const val DATABASE_NAME = "blockchain"
    }

    abstract fun banknotesDao(): BanknotesDao
    abstract fun blockDao(): BlockDao
    abstract fun walletTransactionsDao(): TransactionsDao
}