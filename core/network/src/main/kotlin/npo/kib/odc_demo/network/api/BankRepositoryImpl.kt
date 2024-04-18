package npo.kib.odc_demo.feature_app.data.api

import kotlinx.coroutines.*
import npo.kib.odc_demo.feature_app.domain.core.Wallet
import npo.kib.odc_demo.feature_app.domain.core.getStringPem
import npo.kib.odc_demo.feature_app.domain.model.connection_status.ServerConnectionStatus
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.Banknote
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithProtectedBlock
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.ProtectedBlock
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.bank_api.*
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.Block
import npo.kib.odc_demo.feature_app.domain.repository.BankRepository
import npo.kib.odc_demo.feature_app.domain.util.log
import npo.kib.odc_demo.network.api.BankApi

class BankRepositoryImpl(
    private val bankApi: BankApi, private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : BankRepository {
    override suspend fun getCredentials(): CredentialsResponse {
        return bankApi.getCredentials()
    }

    override suspend fun registerWallet(wr: WalletRequest): WalletResponse {
        return bankApi.registerWallet(wr)
    }


    /**
     * Receiving banknotes from the bank
     * @param amount Required amount of banknotes
     */
    override suspend fun issueBanknotes(
        wallet: Wallet,
        amount: Int,
        walletInsertionCallback: suspend (BanknoteWithProtectedBlock, Block) -> Unit
    ): ServerConnectionStatus {
        return withContext(ioDispatcher) {
            val request = IssueRequest(amount, wallet.walletId)
            val issueResponse = try {
                bankApi.issueBanknotes(request)
            } catch (e: Exception) {
                return@withContext ServerConnectionStatus.ERROR
            }

            val rawBanknotes = issueResponse.issuedBanknotes
                ?: return@withContext ServerConnectionStatus.WALLET_ERROR
            val banknotes = parseBanknotes(rawBanknotes)

            try {
                coroutineScope {
                    banknotes.map { banknote ->
                        ensureActive()
                        wallet.banknoteVerification(banknote)
                        val (block, protectedBlock) = wallet.firstBlock(banknote)
                        async {
                            BanknoteWithProtectedBlock(
                                banknote = banknote, protectedBlock = protectedBlock
                            ) to receiveBanknoteBlock(
                                wallet, block, protectedBlock
                            )
                        }
                    }.forEach {
                        val registered = it.await()
                        walletInsertionCallback(registered.first, registered.second)
                    }
                }
            } catch (e: Exception) {
                this@BankRepositoryImpl.log(e)
                return@withContext ServerConnectionStatus.ERROR
            }
            return@withContext ServerConnectionStatus.SUCCESS
        }
    }


    private suspend fun receiveBanknoteBlock(
        wallet: Wallet, block: Block, protectedBlock: ProtectedBlock
    ): Block {
        val request = ReceiveRequest(
            bnid = block.bnid, otok = block.otok.getStringPem(),
            otokSignature = protectedBlock.otokSignature, time = block.time,
            transactionSign = protectedBlock.transactionSignature, uuid = block.uuid.toString(),
            wid = wallet.walletId
        )
        val response = bankApi.receiveBanknote(request)
        val fullBlock = Block(
            uuid = block.uuid, parentUuid = null, bnid = block.bnid, otok = block.otok,
            time = block.time, magic = response.magic, transactionHash = response.transactionHash,
            transactionHashSignature = response.transactionHashSigned
        )
        wallet.firstBlockVerification(fullBlock)
        return fullBlock
    }

    private suspend fun parseBanknotes(banknotesRaw: List<BanknoteRaw>): List<Banknote> =
        withContext(ioDispatcher) {
            banknotesRaw.map {
                ensureActive()
                Banknote(
                    bin = it.bin.toInt(), amount = it.amount, code = it.code, bnid = it.bnid,
                    signature = it.signature, time = it.time
                )
            }
        }

}