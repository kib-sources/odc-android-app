package npo.kib.odc_demo.feature_app.domain.repository

import npo.kib.odc_demo.common.core.Wallet

interface WalletRepository {

    val userName : String

    suspend fun getOrRegisterWallet(): Wallet

    fun isWalletRegistered(): Boolean

    suspend fun getStoredInWalletSum() : Int?
}