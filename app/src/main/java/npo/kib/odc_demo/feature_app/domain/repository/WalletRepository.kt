package npo.kib.odc_demo.feature_app.domain.repository

import npo.kib.odc_demo.feature_app.data.db.Amount
import npo.kib.odc_demo.feature_app.domain.core.Wallet
import npo.kib.odc_demo.feature_app.domain.model.connection_status.ServerConnectionStatus
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.Banknote
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithBlockchain
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithProtectedBlock
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.ProtectedBlock
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.AcceptanceBlocks
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.Block
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.UserInfo

interface WalletRepository {

    suspend fun isWalletRegistered(): Boolean
    suspend fun getOrRegisterWallet(): Wallet
    suspend fun getStoredInWalletSum(): Int?
    suspend fun getLocalUserInfo() : UserInfo

    //Wallet operations
    suspend fun walletBanknoteVerification(banknote: Banknote)
    suspend fun walletFirstBlock(banknote: Banknote): Pair<Block, ProtectedBlock>
    suspend fun walletFirstBlockVerification(block: Block)
    //A
    suspend fun walletInitProtectedBlock(protectedBlock: ProtectedBlock) : ProtectedBlock
    //B
    suspend fun walletAcceptanceInit(blocks: List<Block>, protectedBlock: ProtectedBlock): AcceptanceBlocks
    //A
    suspend fun walletSignature(parentBlock: Block, childBlock: Block, protectedBlock: ProtectedBlock): Block

    /**
     * Should return *null* if any of the bnids did not correspond to a banknote
     * */
    suspend fun getBanknotesWithBlockchainByBnids(bnidList: List<String>): List<BanknoteWithBlockchain>?
    suspend fun deleteBanknotesWithBlockchainByBnids(bnidList: List<String>)

    /**
     *  Adds each of the banknotes' [BanknoteWithProtectedBlock] and [Block] to the database
     * */
    suspend fun addBanknotesToWallet(banknotes: List<BanknoteWithBlockchain>)
    suspend fun insertBanknote(banknoteWithProtectedBlock: BanknoteWithProtectedBlock)
    suspend fun getBanknotesIdsAndAmounts(): List<Amount>
    /**
     *  Right now it is assumed that there is a banknote associated with the passed *bnid*
     * */
    suspend fun getBanknoteByBnid(requiredBnid: String): BanknoteWithProtectedBlock
    suspend fun deleteBanknoteByBnid(bnid: String)
    suspend fun issueBanknotes(
        amount: Int,
    ): ServerConnectionStatus


    suspend fun insertBlock(block: Block)
    suspend fun getBlocksByBnid(requiredBnid: String): List<Block>

}