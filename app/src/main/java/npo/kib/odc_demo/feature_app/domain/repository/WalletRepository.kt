package npo.kib.odc_demo.feature_app.domain.repository

import npo.kib.odc_demo.common.core.Wallet
import npo.kib.odc_demo.feature_app.data.api.BankApi
import npo.kib.odc_demo.feature_app.data.db.BlockchainDatabase

interface WalletRepository {

    val userName : String

    val blockchainDatabase : BlockchainDatabase

    val bankApi : BankApi
    suspend fun getOrRegisterWallet(): Wallet

    fun isWalletRegistered(): Boolean

    suspend fun getStoredInWalletSum() : Int?
}