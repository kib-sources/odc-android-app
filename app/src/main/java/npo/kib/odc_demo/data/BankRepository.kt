package npo.kib.odc_demo.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.stateIn
import npo.kib.odc_demo.core.Wallet
import npo.kib.odc_demo.core.getStringPem
import npo.kib.odc_demo.core.models.Banknote
import npo.kib.odc_demo.core.models.BanknoteWithProtectedBlock
import npo.kib.odc_demo.core.models.Block
import npo.kib.odc_demo.core.models.ProtectedBlock
import npo.kib.odc_demo.data.api.RetrofitFactory
import npo.kib.odc_demo.data.db.BlockchainDatabase
import npo.kib.odc_demo.data.models.BanknoteRaw
import npo.kib.odc_demo.data.models.IssueRequest
import npo.kib.odc_demo.data.models.ReceiveRequest
import npo.kib.odc_demo.data.models.ServerConnectionStatus
import npo.kib.odc_demo.myLogs

class BankRepository(context: Context) {

    private val db = BlockchainDatabase.getInstance(context)
    private val banknotesDao = db.banknotesDao()
    private val blockDao = db.blockDao()

    private val walletRepository = WalletRepository(context)

    private val bankApi = RetrofitFactory.getBankApi()

    suspend fun getSum() = banknotesDao.getStoredSum()

    fun getSumAsFlow() = banknotesDao.getStoredSumAsFlow()

    /**
     * Receiving banknotes from the bank
     * @param amount Required amount of banknotes
     */
    suspend fun issueBanknotes(amount: Int): ServerConnectionStatus {
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
                        ) to receiveBanknote(wallet, block, protectedBlock)
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

    fun isWalletRegistered() = walletRepository.isWalletRegistered()
}