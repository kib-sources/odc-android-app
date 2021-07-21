package npo.kib.odc_demo.data

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import npo.kib.odc_demo.core.*
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
    private val url2 = "http://46.151.157.153:5000"
    private val bokKey = "BOK"
    private val sokSignKey = "SOK_signed"
    private val widKey = "WID"
    private val prefs = context.getSharedPreferences("openKeys", AppCompatActivity.MODE_PRIVATE)
    private val editor = prefs.edit()

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
        if ((sokSignature == null) || (wid == null)) {
            Log.d("OpenDigitalCashWal", "getting sok_sign and wid from server")
            val walletResp =
                retrofit.registerWallet(WalletRequest("-----BEGIN RSA PUBLIC KEY-----\n${sok.getString()}-----END RSA PUBLIC KEY-----"))
            Log.d("OpenDigitalCashWal", walletResp.sokSignature)
            Log.d("OpenDigitalCashWal", walletResp.wid)
            sokSignature = walletResp.sokSignature
            wid = walletResp.wid
            editor.putString(sokSignKey, sokSignature)
            editor.apply()
            editor.putString(widKey, wid)
            editor.apply()
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
        val request = IssueRequest(amount, wallet.wid)
        val issueResponse = retrofit.issueBanknotes(request)
        val rawBanknotes = issueResponse.issuedBanknotes
        val blockchain = ArrayList<Triple<Banknote, Block, ProtectedBlock>>()
        if (rawBanknotes.isNotEmpty()) {
            val banknotes = parseBanknotes(rawBanknotes)
            for (banknote in banknotes) {
                var (block, protectedBlock) = wallet.firstBlock(banknote)
                block = receiveBanknote(wallet, banknote, block, protectedBlock)
                blockchain.add(Triple(banknote, block, protectedBlock))
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
        val retBlock = Block(
            uuid = block.uuid,
            parentUuid = null,
            bnid = block.bnid,
            otok = block.otok,
            magic = response.magic,
            hashValue = response.transactionHash.toByteArray(),
            signature = response.transactionHashSigned
        )
        return retBlock
    }

    private fun parseBanknotes(raw: List<BanknoteRaw>): List<Banknote> {
        val banknotes = ArrayList<Banknote>()
        var banknote: Banknote
        for (r in raw) {
            banknote = Banknote(
                bin = 333,
                amount = r.amount,
                currencyCode = ISO_4217_CODE.from(r.code),
                bnid = r.bnid,
                signature = r.signature,
                time = r.time,
                hashValue = makeBanknoteHashValue(333, r.amount, ISO_4217_CODE.from(r.code), r.bnid)
            )
            banknotes.add(banknote)
        }

        return banknotes
    }
}