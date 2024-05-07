package npo.kib.odc_demo.core.network.api

import npo.kib.odc_demo.core.model.bank_api.*
import npo.kib.odc_demo.core.wallet.Wallet
import npo.kib.odc_demo.core.wallet.model.BanknoteWithProtectedBlock
import npo.kib.odc_demo.core.wallet.model.data_packet.variants.Block


interface BankRepository {

    suspend fun getCredentials(): CredentialsResponse

    suspend fun registerWallet(wr: WalletRequest): WalletResponse

    suspend fun issueBanknotes(
        wallet: Wallet,
        amount: Int,
        walletInsertionCallback: suspend (BanknoteWithProtectedBlock, Block) -> Unit
    ): ServerConnectionStatus

}