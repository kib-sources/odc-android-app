package npo.kib.odc_demo

import android.app.Application
import npo.kib.odc_demo.data.db.BlockchainDatabase

class App : Application() {

    private lateinit var database: BlockchainDatabase

    override fun onCreate() {
        super.onCreate()
        database = BlockchainDatabase.getInstance(this)
    }

    fun getDatabase() = database
}