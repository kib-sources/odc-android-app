package npo.kib.odc_demo.network.api

import kotlinx.coroutines.*
import npo.kib.odc_demo.common.data.util.log
import npo.kib.odc_demo.wallet.Crypto.asPemString
import npo.kib.odc_demo.wallet.Wallet
import npo.kib.odc_demo.wallet.model.Banknote
import npo.kib.odc_demo.wallet.model.BanknoteWithProtectedBlock
import npo.kib.odc_demo.wallet.model.ProtectedBlock
import npo.kib.odc_demo.wallet.model.data_packet.variants.Block

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
            bnid = block.bnid, otok = block.otok.asPemString(),
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
                    bin = it.bin.toInt(), amount = it.amount, currencyCode = it.code, bnid = it.bnid,
                    signature = it.signature, time = it.time
                )
            }
        }

}