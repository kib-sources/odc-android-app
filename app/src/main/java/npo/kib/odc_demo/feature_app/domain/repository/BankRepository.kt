package npo.kib.odc_demo.feature_app.domain.repository

import kotlinx.coroutines.flow.Flow
import npo.kib.odc_demo.feature_app.data.api.BankApi
import npo.kib.odc_demo.feature_app.domain.model.connection_status.ServerConnectionStatus

interface BankRepository {

    val walletRepository: WalletRepository

    val bankApi: BankApi
    suspend fun getSum(): Int?

    fun getSumAsFlow(): Flow<Int?>

    suspend fun issueBanknotes(amount: Int): ServerConnectionStatus

    fun isWalletRegistered(): Boolean
}