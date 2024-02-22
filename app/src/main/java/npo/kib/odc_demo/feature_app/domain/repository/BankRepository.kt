package npo.kib.odc_demo.feature_app.domain.repository

import npo.kib.odc_demo.feature_app.domain.core.Wallet
import npo.kib.odc_demo.feature_app.domain.model.connection_status.ServerConnectionStatus
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithProtectedBlock
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.bank_api.CredentialsResponse
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.bank_api.WalletRequest
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.bank_api.WalletResponse
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.Block

interface BankRepository {

    suspend fun getCredentials(): CredentialsResponse

    suspend fun registerWallet(wr: WalletRequest): WalletResponse

    suspend fun issueBanknotes(
        wallet: Wallet,
        amount: Int,
        walletInsertionCallback: suspend (BanknoteWithProtectedBlock, Block) -> Unit
    ): ServerConnectionStatus

}