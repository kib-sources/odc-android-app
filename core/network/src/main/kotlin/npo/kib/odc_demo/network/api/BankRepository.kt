package npo.kib.odc_demo.network.api

import npo.kib.odc_demo.wallet.Wallet
import npo.kib.odc_demo.wallet.model.BanknoteWithProtectedBlock
import npo.kib.odc_demo.wallet.model.data_packet.variants.Block


interface BankRepository {

    suspend fun getCredentials(): CredentialsResponse

    suspend fun registerWallet(wr: WalletRequest): WalletResponse

    suspend fun issueBanknotes(
        wallet: Wallet,
        amount: Int,
        walletInsertionCallback: suspend (BanknoteWithProtectedBlock, Block) -> Unit
    ): ServerConnectionStatus

}