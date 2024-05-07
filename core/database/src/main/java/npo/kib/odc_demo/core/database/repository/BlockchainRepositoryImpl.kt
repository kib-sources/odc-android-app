package npo.kib.odc_demo.core.database.repository

import npo.kib.odc_demo.core.database.dao.BanknotesDao
import npo.kib.odc_demo.core.database.dao.BlockDao
import npo.kib.odc_demo.core.database.model.asDatabaseEntity
import npo.kib.odc_demo.core.database.model.asDomainModel
import npo.kib.odc_demo.core.wallet.model.Amount
import npo.kib.odc_demo.core.wallet.model.BanknoteWithProtectedBlock
import npo.kib.odc_demo.core.wallet.model.data_packet.variants.Block

class BlockchainRepositoryImpl(private val blockDao: BlockDao, private val banknotesDao: BanknotesDao) :
    BlockchainRepository {

    override suspend fun insertBlock(block: Block) {
        blockDao.insertBlock(block.asDatabaseEntity())
    }

    override suspend fun getBlocksByBnid(requiredBnid: String): List<Block> {
        return blockDao.getBlocksByBnid(requiredBnid).map { it.asDomainModel() }
    }

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