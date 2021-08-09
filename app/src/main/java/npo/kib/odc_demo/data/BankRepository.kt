package npo.kib.odc_demo.data

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import npo.kib.odc_demo.core.*
import npo.kib.odc_demo.data.db.BlockchainDatabase
import npo.kib.odc_demo.data.models.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.PrivateKey
import java.security.PublicKey

class BankRepository(context: Context) {

    private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }
    private val okHttpClient = OkHttpClient.Builder().apply {
        this.addInterceptor(interceptor)
    }.build()
    private val url = "http://31.186.250.158:80"
    private val bokKey = "BOK"
    private val sokSignKey = "SOK_signed"
    private val widKey = "WID"
    private val bin = 333
    private val prefs = context.getSharedPreferences("openKeys", AppCompatActivity.MODE_PRIVATE)
    private val editor = prefs.edit()
    private val db =
        Room.databaseBuilder(context, BlockchainDatabase::class.java, "blockchain").build()
    private val blockchainDao = db.blockchainDao()

    private val retrofit = Retrofit
        .Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(BankApi::class.java)

    private suspend fun getBok(): String {
        var bok = prefs.getString(bokKey, null)
        if (bok == null) {
            Log.d("OpenDigitalCash", "getting bok from server")
            val bokResponse = retrofit.getBok()
            bok = bokResponse.bok
            editor.putString(bokKey, bok)
            editor.apply()
        }
        Log.d("OpenDigitalCash", "bok is = $bok")
        return bok
    }

    suspend fun registerWallet(): Wallet {
        var keys: Pair<PublicKey, PrivateKey>
        try {
            keys = Crypto.getSimKeys()
        } catch (e: NullPointerException) {
            keys = Crypto.initSkp()
            editor.clear().apply()
        }
        val sok = keys.first
        val spk = keys.second
        var sokSignature = prefs.getString(sokSignKey, null)
        var wid = prefs.getString(widKey, null)
        if (sokSignature != null) {
            Log.d("OpenDigitalCashQ", sokSignature)
        }
        if (wid != null) {
            Log.d("OpenDigitalCashQ", wid)
        }
        if (sokSignature == null || wid == null) {
            Log.d("OpenDigitalCashWal", "getting sok_sign and wid from server")
            val walletResp =
                retrofit.registerWallet(WalletRequest("-----BEGIN RSA PUBLIC KEY-----\n${sok.getString()}-----END RSA PUBLIC KEY-----"))
            Log.d("OpenDigitalCashWal", walletResp.sokSignature)
            Log.d("OpenDigitalCashWal", walletResp.wid)
            sokSignature = walletResp.sokSignature
            wid = walletResp.wid
            editor.putString(sokSignKey, sokSignature).putString(widKey, wid).apply()
        }
        val bok = loadPublicKey(getBok())
        val wallet = Wallet(spk, sok, sokSignature, bok, wid)
        Log.d("OpenDigitalCashWal", wallet.toString())
        return wallet
    }

    suspend fun issueBanknotes(
        amount: Int,
        wallet: Wallet
    ): ArrayList<Triple<Banknote, Block, ProtectedBlock>> {
        Log.d("OpenDigitalCashT3", Thread.currentThread().name)
        val request = IssueRequest(amount, wallet.wid)
        val issueResponse = retrofit.issueBanknotes(request)
        val rawBanknotes = issueResponse.issuedBanknotes
        val blockchain = ArrayList<Triple<Banknote, Block, ProtectedBlock>>()
        if (rawBanknotes.isNotEmpty()) {
            val banknotes = parseBanknotes(rawBanknotes)
            for (banknote in banknotes) {
                var (block, protectedBlock) = wallet.firstBlock(banknote)
                block = receiveBanknote(wallet, banknote, block, protectedBlock)
                blockchainDao.insertAll(
                    Blockchain(
                        banknote = banknote,
                        block = block,
                        protectedBlock = protectedBlock
                    )
                )
            }
        }
        return blockchain
    }

    private suspend fun receiveBanknote(
        wallet: Wallet,
        banknote: Banknote,
        block: Block,
        protectedBlock: ProtectedBlock
    ): Block {
        val request = ReceiveRequest(
            bnid = block.bnid,
            otok = "-----BEGIN RSA PUBLIC KEY-----\n${block.otok.getString()}-----END RSA PUBLIC KEY-----",
            otokSignature = protectedBlock.otokSignature,
            time = banknote.time,
            transactionSign = protectedBlock.transactionSignature,
            uuid = block.uuid.toString(),
            wid = wallet.wid
        )
        val response = retrofit.receiveBanknote(request)
        return Block(
            uuid = block.uuid,
            parentUuid = null,
            bnid = block.bnid,
            otok = block.otok,
            magic = response.magic,
            hashValue = response.transactionHash.toByteArray(),
            signature = response.transactionHashSigned
        )
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
                time = r.time,
                hashValue = makeBanknoteHashValue(333, r.amount, r.code, r.bnid)
            )
            banknotes.add(banknote)
        }

        return banknotes
    }

    fun getSum() = blockchainDao.getSum()

    suspend fun getBlockchainsByAmount(requiredAmount: Int) {
        //TODO обработать ситуацию, когда не хватает банкнот для выдачи точной суммы
        var amount = requiredAmount
        val banknoteAmounts = blockchainDao.getBnidsAndAmounts()
        banknoteAmounts.sortedByDescending { amounts -> amounts.amount }
        val blockchainsList = arrayListOf<Blockchain>()
        for (banknoteAmount in banknoteAmounts) {
            if (amount >= banknoteAmount.amount){
                blockchainsList.add(blockchainDao.getBlockchainByBnid(banknoteAmount.bnid))
                amount -= banknoteAmount.amount
            }
            if (amount <= 0) break
        }
    }
}