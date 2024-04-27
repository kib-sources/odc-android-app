package npo.kib.odc_demo.database.repository

import npo.kib.odc_demo.database.BlockchainDatabase
import npo.kib.odc_demo.database.dao.BanknotesDao
import npo.kib.odc_demo.database.dao.BlockDao
import npo.kib.odc_demo.database.model.asDatabaseEntity
import npo.kib.odc_demo.database.model.asDomainModel
import npo.kib.odc_demo.wallet.model.Amount
import npo.kib.odc_demo.wallet.model.BanknoteWithProtectedBlock
import npo.kib.odc_demo.wallet.model.data_packet.variants.Block

class BlockchainRepositoryImpl(blockchainDb: BlockchainDatabase) : BlockchainRepository {

    private val blockDao: BlockDao = blockchainDb.blockDao

    override suspend fun insertBlock(block: Block) {
        blockDao.insertBlock(block.asDatabaseEntity())
    }

    override suspend fun getBlocksByBnid(requiredBnid: String): List<Block> {
        return blockDao.getBlocksByBnid(requiredBnid).map { it.asDomainModel() }
    }


    private val banknotesDao: BanknotesDao = blockchainDb.banknotesDao

    override suspend fun insertBanknote(banknoteWithProtectedBlock: BanknoteWithProtectedBlock) {
        banknotesDao.insertBanknote(banknoteWithProtectedBlock.asDatabaseEntity())
    }

    override suspend fun getBnidsAndAmounts(): List<Amount> =
        banknotesDao.getBnidsAndAmounts().map { it.asDomainModel() }


    override suspend fun getBanknoteByBnid(requiredBnid: String): BanknoteWithProtectedBlock =
        banknotesDao.getBanknoteByBnid(requiredBnid).asDomainModel()

    override suspend fun getStoredSum(): Int? = banknotesDao.getStoredSum()

    override suspend fun deleteBanknoteByBnid(bnid: String) {
        banknotesDao.deleteBanknoteByBnid(bnid)
    }

}