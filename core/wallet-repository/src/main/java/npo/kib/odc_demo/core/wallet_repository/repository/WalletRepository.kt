package npo.kib.odc_demo.core.wallet_repository.repository


import npo.kib.odc_demo.core.model.bank_api.ServerConnectionStatus
import npo.kib.odc_demo.core.wallet.model.*
import npo.kib.odc_demo.core.wallet.model.data_packet.variants.AcceptanceBlocks
import npo.kib.odc_demo.core.wallet.model.data_packet.variants.Block
import npo.kib.odc_demo.core.wallet.model.data_packet.variants.UserInfo

interface WalletRepository {

    suspend fun isWalletRegistered(): Boolean
    suspend fun registerWallet(): Result<Boolean>
    suspend fun getStoredInWalletSum(): Int?
    suspend fun getWalletId(): String?
    suspend fun getLocalUserInfo(): UserInfo

    //Wallet operations
    suspend fun walletBanknoteVerification(banknote: Banknote)
    suspend fun walletFirstBlock(banknote: Banknote): Pair<Block, ProtectedBlock>
    suspend fun walletFirstBlockVerification(block: Block)

    //A
    suspend fun walletInitProtectedBlock(protectedBlock: ProtectedBlock): ProtectedBlock

    //B
    /**Create the new [ProtectedBlock] for the banknote from the old [protectedBlock] and create a new
     * [childBlock][Block] based on the last parent block from the [blocks]*/
    suspend fun walletAcceptanceInit(
        blocks: List<Block>, protectedBlock: ProtectedBlock
    ): AcceptanceBlocks

    //A
    /**
     *  Signature of a new unsigned childBlock.
     *  1.  Verifying
     *       - That the new childBlock is created within last 60 seconds prior to the current time
     *       - Protected block's signature,
     *       - Child block's signature
     *  2. Signing the childBlock and returning the new signed block from the function
     *
     * */
    suspend fun walletSignature(
        parentBlock: Block, childBlock: Block, protectedBlock: ProtectedBlock
    ): Block

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