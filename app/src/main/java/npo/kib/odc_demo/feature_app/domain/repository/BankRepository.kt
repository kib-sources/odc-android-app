package npo.kib.odc_demo.feature_app.domain.repository

import kotlinx.coroutines.flow.Flow
import npo.kib.odc_demo.feature_app.domain.model.types.ServerConnectionStatus

interface BankRepository {

    suspend fun getSum(): Int?

    fun getSumAsFlow(): Flow<Int?>

    suspend fun issueBanknotes(amount: Int): ServerConnectionStatus

    fun isWalletRegistered(): Boolean
}