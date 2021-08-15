package npo.kib.odc_demo.data

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import npo.kib.odc_demo.core.*
import npo.kib.odc_demo.data.db.BlockchainDatabase
import npo.kib.odc_demo.data.models.*
import npo.kib.odc_demo.data.p2p.ObjectSerializer
import npo.kib.odc_demo.data.p2p.P2PConnection
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.PrivateKey
import java.security.PublicKey

class BankRepository(context: Context) {

    private val interceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
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

    private val p2p = P2PConnection(context)
    private val serializer = ObjectSerializer()
    val connectionResult = p2p.connectionResult

    private val retrofit = Retrofit
        .Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(BankApi::class.java)

    init {
        var job: Job = Job()
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("OpenDigitalCashT", "Корутина выполняется на потоке: ${Thread.currentThread().name}")
            connectionResult.collect {
                if (it is ConnectingStatus.ConnectionResult) {
                    if (it.result.status.statusCode == ConnectionsStatusCodes.STATUS_OK) {
                        job = p2p.receivedBytes.onEach { bytes -> receiveBytes(bytes) }.launchIn(this)
                    }
                } else job.cancel()
            }
        }
    }

    private suspend fun getBok(): String {
        var bok = prefs.getString(bokKey, null)
        if (bok == null) {
            Log.d("OpenDigitalCash", "getting bok from server")
            val bokResponse = retrofit.getBok()
            bok = bokResponse.bok
            editor.putString(bokKey, bok)
            editor.apply()
        }
        Log.d("OpenDigitalCashB", "bok is = $bok")
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
        val bok = loadPublicKey(getBok())
        if (sokSignature == null || wid == null) {
            Log.d("OpenDigitalCashWal", "getting sok_sign and wid from server")
            val walletResp =
                retrofit.registerWallet(WalletRequest("-----BEGIN RSA PUBLIC KEY-----\n${sok.getString()}-----END RSA PUBLIC KEY-----"))
            Log.d("OpenDigitalCashWal", walletResp.sokSignature)
            Log.d("OpenDigitalCashWal", walletResp.wid)
            sokSignature = walletResp.sokSignature
            val sokHash =
                Crypto.hash("-----BEGIN RSA PUBLIC KEY-----\n${sok.getString()}-----END RSA PUBLIC KEY-----")
            if (!Crypto.verifySignature(sokHash, sokSignature, bok)) {
                throw Exception("Подпись SOK недействительна")
            }
            wid = walletResp.wid
            editor.putString(sokSignKey, sokSignature).putString(widKey, wid).apply()
        }
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
            otok = "-----BEGIN RSA PUBLIC KEY-----\n${block.otok?.getString()}-----END RSA PUBLIC KEY-----",
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
            transactionHashValue = response.transactionHash.toByteArray(),
            transactionHashSignature = response.transactionHashSigned
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

    private suspend fun getBlockchainsByAmount(requiredAmount: Int): ArrayList<Blockchain> {
        //TODO обработать ситуацию, когда не хватает банкнот для выдачи точной суммы
        var amount = requiredAmount
        val banknoteAmounts = blockchainDao.getBnidsAndAmounts().toCollection(ArrayList())
        banknoteAmounts.sortByDescending { it.amount }
        val blockchainsList = ArrayList<Blockchain>()
        for (banknoteAmount in banknoteAmounts) {
            if (amount >= banknoteAmount.amount) {
                Log.d(
                    "OpenDigitalCashS",
                    banknoteAmount.amount.toString() + "   " + banknoteAmount.bnid
                )
                blockchainsList.add(blockchainDao.getBlockchainByBnid(banknoteAmount.bnid))
                amount -= banknoteAmount.amount
            }
            if (amount <= 0) break
        }
        return blockchainsList
    }

//P2P Connection

    fun startAdvertising() {
        p2p.startAdvertising()
    }

    fun startDiscovery() {
        p2p.startDiscovery()
    }

    fun acceptConnection() {
        p2p.acceptConnection()
    }

    fun rejectConnection() {
        p2p.rejectConnection()
    }

    suspend fun send(amount: Int) {
        val blockchainArray = getBlockchainsByAmount(amount)
        val payloadContainer = PayloadContainer(1, blockchainArray)
        val blockchainJson = serializer.toJson(payloadContainer)
        Log.d("OpenDigitalCashJ", blockchainJson)
        p2p.send(blockchainJson.encodeToByteArray())
    }

    private suspend fun receiveBytes(bytes: ByteArray) {
        val container = serializer.toObject(bytes.decodeToString())
        Log.d("OpenDigitalCashJ", bytes.decodeToString())
        Log.d("OpenDigitalCashJ", container.blockchainsList?.first().toString())
        container.blockchainsList?.forEach { blockchain ->
            val wallet = registerWallet()
            val (childBlock, childProtectedBlock) = wallet.acceptanceInit(blockchain.block, blockchain.protectedBlock)
        }
    }


}