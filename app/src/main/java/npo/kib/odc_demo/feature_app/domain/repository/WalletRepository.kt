package npo.kib.odc_demo.feature_app.domain.repository

import npo.kib.odc_demo.feature_app.data.db.Amounts
import npo.kib.odc_demo.feature_app.domain.core.Wallet
import npo.kib.odc_demo.feature_app.domain.model.connection_status.ServerConnectionStatus
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithBlockchain
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithProtectedBlock
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.Block

interface WalletRepository {

    suspend fun isWalletRegistered(): Boolean
    suspend fun getOrRegisterWallet(): Wallet
    suspend fun getStoredInWalletSum(): Int?


    suspend fun getBanknotesFromWallet(): List<BanknoteWithBlockchain>
    suspend fun addBanknotesToWallet(banknotes: List<BanknoteWithBlockchain>)
    suspend fun insertBanknote(banknoteWithProtectedBlock: BanknoteWithProtectedBlock)
    suspend fun getBanknotesIdsAndAmounts(): List<Amounts>
    suspend fun getBanknoteByBnid(requiredBnid: String): BanknoteWithProtectedBlock
    suspend fun deleteBanknoteByBnid(bnid: String)
    suspend fun issueBanknotes(
        amount: Int,
    ): ServerConnectionStatus


    suspend fun insertBlock(block: Block)
    suspend fun getBlocksByBnid(requiredBnid: String): List<Block>

}