package npo.kib.odc_demo.database.repository

import npo.kib.odc_demo.wallet.model.Amount
import npo.kib.odc_demo.wallet.model.BanknoteWithProtectedBlock
import npo.kib.odc_demo.wallet.model.data_packet.variants.Block

interface BlockchainRepository {

    suspend fun insertBlock(block: Block)

    suspend fun getBlocksByBnid(requiredBnid: String): List<Block>

    suspend fun insertBanknote(banknoteWithProtectedBlock: BanknoteWithProtectedBlock)

    suspend fun getBnidsAndAmounts(): List<Amount>

    suspend fun getBanknoteByBnid(requiredBnid: String): BanknoteWithProtectedBlock

    suspend fun getStoredSum(): Int?

    suspend fun deleteBanknoteByBnid(bnid: String)

}