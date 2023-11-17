package npo.kib.odc_demo.feature_app.data.repositories

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import npo.kib.odc_demo.common.core.Wallet
import npo.kib.odc_demo.common.core.getStringPem
import npo.kib.odc_demo.common.core.models.Banknote
import npo.kib.odc_demo.common.core.models.BanknoteWithProtectedBlock
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.Block
import npo.kib.odc_demo.common.core.models.ProtectedBlock
import npo.kib.odc_demo.common.util.myLogs
import npo.kib.odc_demo.feature_app.data.api.BankApi
import npo.kib.odc_demo.feature_app.domain.model.serialization.BanknoteRaw
import npo.kib.odc_demo.feature_app.domain.model.serialization.IssueRequest
import npo.kib.odc_demo.feature_app.domain.model.serialization.ReceiveRequest
import npo.kib.odc_demo.feature_app.domain.model.connection_status.ServerConnectionStatus
import npo.kib.odc_demo.feature_app.domain.repository.BankRepository
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository

class BankRepositoryImpl(override val walletRepository: WalletRepository) : BankRepository {

    private val banknotesDao = walletRepository.blockchainDatabase.banknotesDao
    private val blockDao = walletRepository.blockchainDatabase.blockDao

    override val bankApi : BankApi = walletRepository.bankApi

    override suspend fun getSum() = banknotesDao.getStoredSum()

    override fun getSumAsFlow() = banknotesDao.getStoredSumAsFlow()

    /**
     * Receiving banknotes from the bank
     * @param amount Required amount of banknotes
     */
    override suspend fun issueBanknotes(amount: Int): ServerConnectionStatus {
        val wallet = try {
            walletRepository.getOrRegisterWallet()
        } catch (e: Exception) {
            return ServerConnectionStatus.WALLET_ERROR
        }

        val request = IssueRequest(amount, wallet.wid)
        val issueResponse = try {
            bankApi.issueBanknotes(request)
        } catch (e: Exception) {
            return ServerConnectionStatus.ERROR
        }

        val rawBanknotes = issueResponse.issuedBanknotes
            ?: return ServerConnectionStatus.WALLET_ERROR
        val banknotes = parseBanknotes(rawBanknotes)

        try {
            coroutineScope {
                banknotes.map { banknote ->
                    wallet.banknoteVerification(banknote)
                    val (block, protectedBlock) = wallet.firstBlock(banknote)
                    async {
                        BanknoteWithProtectedBlock(
                            banknote = banknote,
                            protectedBlock = protectedBlock
                                                  ) to receiveBanknote(
                            wallet,
                            block,
                            protectedBlock)
                    }
                }.forEach {
                    val registered = it.await()
                    banknotesDao.insert(registered.first)
                    blockDao.insert(registered.second)
                }
            }
        } catch (e: Exception) {
            myLogs(e)
            return ServerConnectionStatus.ERROR
        }
        return ServerConnectionStatus.SUCCESS
    }

    override fun isWalletRegistered() = walletRepository.isWalletRegistered()

    private suspend fun receiveBanknote(
        wallet: Wallet,
        block: Block,
        protectedBlock: ProtectedBlock
                                       ): Block {
        val request = ReceiveRequest(
            bnid = block.bnid,
            otok = block.otok.getStringPem(),
            otokSignature = protectedBlock.otokSignature,
            time = block.time,
            transactionSign = protectedBlock.transactionSignature,
            uuid = block.uuid.toString(),
            wid = wallet.wid
                                    )
        val response = bankApi.receiveBanknote(request)
        val fullBlock = Block(
            uuid = block.uuid,
            parentUuid = null,
            bnid = block.bnid,
            otok = block.otok,
            time = block.time,
            magic = response.magic,
            transactionHash = response.transactionHash,
            transactionHashSignature = response.transactionHashSigned
                             )
        wallet.firstBlockVerification(fullBlock)
        return fullBlock
    }

    private fun parseBanknotes(banknotesRaw: List<BanknoteRaw>): List<Banknote> {
        return banknotesRaw.map {
            Banknote(
                bin = it.bin.toInt(),
                amount = it.amount,
                code = it.code,
                bnid = it.bnid,
                signature = it.signature,
                time = it.time
                    )
        }
    }

}