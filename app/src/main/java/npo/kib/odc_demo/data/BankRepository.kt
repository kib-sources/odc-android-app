package npo.kib.odc_demo.data

import npo.kib.odc_demo.App
import npo.kib.odc_demo.core.*
import npo.kib.odc_demo.data.models.*
import npo.kib.odc_demo.decodeHex
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.collections.ArrayList

class BankRepository(app: App) {

    private val interceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
    private val url = "http://31.186.250.158:80"

    private val bin = 333

    private val db = app.getDatabase()
    private val blockchainDao = db.blockchainDao()
    private val blockDao = db.blockDao()

    private val walletRepository = WalletRepository(app)

    private val retrofit = Retrofit
        .Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(BankApi::class.java)

    suspend fun getSum() = blockchainDao.getSum()

    /**
     * Receiving banknotes from the bank
     * @param amount Required amount of banknotes
     */
    suspend fun issueBanknotes(amount: Int): ServerConnectionStatus {
        val wallet = try {
            walletRepository.getWallet()
        } catch (e: Exception) {
            return ServerConnectionStatus.WALLET_ERROR
        }
        val request = IssueRequest(amount, wallet.wid)
        val issueResponse = try {
            retrofit.issueBanknotes(request)
        } catch (e: Exception) {
            return ServerConnectionStatus.ERROR
        }
        val rawBanknotes = issueResponse.issuedBanknotes
        val banknotes = parseBanknotes(rawBanknotes)
        for (banknote in banknotes) {
            wallet.banknoteVerification(banknote)
            var (block, protectedBlock) = wallet.firstBlock(banknote)
            try {
                block = receiveBanknote(wallet, block, protectedBlock)
            } catch (e: Exception) {
                return ServerConnectionStatus.ERROR
            }

            blockchainDao.insertAll(
                Blockchain(
                    bnidKey = banknote.bnid,
                    banknote = banknote,
                    protectedBlock = protectedBlock
                )
            )
            blockDao.insertAll(block)
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
        val response = retrofit.receiveBanknote(request)
        val fullBlock = Block(
            uuid = block.uuid,
            parentUuid = null,
            bnid = block.bnid,
            otok = block.otok,
            time = block.time,
            magic = response.magic,
            transactionHashValue = response.transactionHash.decodeHex(),
            transactionHashSignature = response.transactionHashSigned
        )
        wallet.firstBlockVerification(fullBlock)
        return fullBlock
    }

    private fun parseBanknotes(raw: List<BanknoteRaw>): List<Banknote> {
        val banknotes = ArrayList<Banknote>()
        var banknote: Banknote
        for (r in raw) {
            banknote = Banknote(
                bin = bin,
                amount = r.amount,
                currencyCode = r.code,
                bnid = r.bnid,
                signature = r.signature,
                time = r.time
            )
            banknotes.add(banknote)
        }
        return banknotes
    }

    fun isWalletRegistered() = walletRepository.isWalletRegistered()
}