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
import npo.kib.odc_demo.core.Crypto.decodeHex
import npo.kib.odc_demo.data.db.BlockchainDatabase
import npo.kib.odc_demo.data.models.BlockchainFromDB
import npo.kib.odc_demo.data.models.*
import npo.kib.odc_demo.data.p2p.ObjectSerializer
import npo.kib.odc_demo.data.p2p.P2PConnection
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

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
    private val blockDao = db.blockDao()

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
        var job = Job() as Job
        CoroutineScope(Dispatchers.IO).launch {
            connectionResult.collect {
                if (it is ConnectingStatus.ConnectionResult) {
                    if (it.result.status.statusCode == ConnectionsStatusCodes.STATUS_OK) {
                        job =
                            p2p.receivedBytes.onEach { bytes -> receiveBytes(bytes) }.launchIn(this)
                    }
                } else job.cancel()
            }
        }
    }

    //Сервер -> Клиент
    private suspend fun getBok(): String {
        var bok = prefs.getString(bokKey, null)
        if (bok == null) {
            Log.d("OpenDigitalCash", "getting bok from server")
            val bokResponse = retrofit.getBok()
            bok = bokResponse.bok
            editor.putString(bokKey, bok)
            editor.apply()
        }
        return bok
    }

    private suspend fun registerWallet(): Wallet {
        //Берём ключи из keyStore или генерируем новые
        var keys: Pair<PublicKey, PrivateKey>
        try {
            keys = Crypto.getSimKeys()
        } catch (e: NullPointerException) {
            keys = Crypto.initSKP()
            editor.clear().apply()
        }
        val sok = keys.first
        val spk = keys.second
        var sokSignature = prefs.getString(sokSignKey, null)
        var wid = prefs.getString(widKey, null)

        val bok = getBok().loadPublicKey()
        if (sokSignature == null || wid == null) {
            Log.d("OpenDigitalCashWal", "getting sok_sign and wid from server")
            val walletResp =
                retrofit.registerWallet(WalletRequest(sok.getStringPem()))
            sokSignature = walletResp.sokSignature
            verifySokSign(sok, sokSignature, bok)
            wid = walletResp.wid
            editor.putString(sokSignKey, sokSignature).putString(widKey, wid).apply()
        }
        return Wallet(spk, sok, sokSignature, bok, wid)
    }

    private fun verifySokSign(sok: PublicKey, sokSignature: String, bok: PublicKey) {
        val sokHash =
            Crypto.hash(sok.getStringPem())
        if (!Crypto.verifySignature(sokHash, sokSignature, bok)) {
            throw Exception("Подпись SOK недействительна")
        }
    }

    /**
     * Receiving banknotes from the bank
     * @param amount Required amount of banknotes
     */
    suspend fun issueBanknotes(amount: Int) {
        val wallet = registerWallet()
        val request = IssueRequest(amount, wallet.wid)
        val issueResponse = retrofit.issueBanknotes(request)
        val rawBanknotes = issueResponse.issuedBanknotes
        if (rawBanknotes.isNotEmpty()) {
            val banknotes = parseBanknotes(rawBanknotes)
            for (banknote in banknotes) {
                wallet.banknoteVerification(banknote)
                var (block, protectedBlock) = wallet.firstBlock(banknote)
                block = receiveBanknote(wallet, block, protectedBlock)
                blockchainDao.insertAll(
                    Blockchain(
                        bnidKey = banknote.bnid,
                        banknote = banknote,
                        protectedBlock = protectedBlock
                    )
                )
                blockDao.insertAll(block)
            }
        }
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

    fun getSum() = blockchainDao.getSum()

    private suspend fun getBlockchainsByAmount(requiredAmount: Int): ArrayList<BlockchainFromDB> {
        //TODO обработать ситуацию, когда не хватает банкнот для выдачи точной суммы
        var amount = requiredAmount
        val banknoteAmounts = blockchainDao.getBnidsAndAmounts().toCollection(ArrayList())
        banknoteAmounts.sortByDescending { it.amount }
        val blockchainsList = ArrayList<BlockchainFromDB>()
        for (banknoteAmount in banknoteAmounts) {
            if (amount >= banknoteAmount.amount) {
                Log.d(
                    "OpenDigitalCashS",
                    banknoteAmount.amount.toString() + "   " + banknoteAmount.bnid
                )
                blockchainsList.add(
                    BlockchainFromDB(
                        blockchainDao.getBlockchainByBnid(
                            banknoteAmount.bnid
                        ), blockDao.getBlocksByBnid(banknoteAmount.bnid)
                    )
                )
                amount -= banknoteAmount.amount
            }
            if (amount <= 0) break
        }
        return blockchainsList
    }

//Клиент А -> Клиент Б

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

    private val sentBlocks = LinkedHashMap<UUID, Block>()

    /**
     * Sending banknotes to another device
     * @param amount Amount to send
     */
    suspend fun sendBanknotes(amount: Int) {
        //Шаг 1.
        val blockchainArray = getBlockchainsByAmount(amount)
        val wallet = registerWallet()

        for (blockchainFromDB in blockchainArray) {
            //Создание нового ProtectedBlock
            val newProtectedBlock =
                wallet.initProtectedBlock(blockchainFromDB.blockchain.protectedBlock)
            blockchainFromDB.blockchain.protectedBlock = newProtectedBlock

            val payloadContainer = PayloadContainer(blockchain = blockchainFromDB)
            val blockchainJson = serializer.toJson(payloadContainer)
            p2p.send(blockchainJson.encodeToByteArray())

            //Запоминаем отправленный parentBlock для последующей верификации
            val parentBlock = blockchainFromDB.blocks.last()
            sentBlocks[parentBlock.uuid] = parentBlock
        }
    }

    private val blockchainsToDB = LinkedHashMap<String, Blockchain>()
    private val blocksToDB = LinkedHashMap<String, List<Block>>()

    private suspend fun receiveBytes(bytes: ByteArray) {
        val container = serializer.toObject(bytes.decodeToString())
        val wallet = registerWallet()

        if (container.blockchain != null) {
            val blockchainFromDB = container.blockchain

            //Шаг 2-4.
            val blocks = blockchainFromDB.blocks
            val protectedBlockPart = blockchainFromDB.blockchain.protectedBlock

            val childBlocksPair = wallet.acceptanceInit(blocks, protectedBlockPart)
            sendAcceptanceBlocks(childBlocksPair)

            //Запоминаем блокчейн для добавления в бд в случае успешной верификации
            val bnid = blockchainFromDB.blockchain.bnidKey
            blockchainsToDB[bnid] = Blockchain(
                bnidKey = bnid,
                banknote = blockchainFromDB.blockchain.banknote,
                protectedBlock = childBlocksPair.protectedBlock
            )
            blocksToDB[bnid] = blocks
        }

        //Шаг 5.
        if (container.blocks != null) {
            val acceptanceBlocks = container.blocks
            val childBlockFull = wallet.signature(
                sentBlocks[acceptanceBlocks.childBlock.parentUuid]!!,
                acceptanceBlocks.childBlock,
                acceptanceBlocks.protectedBlock
            )
            val block = acceptanceBlocks.childBlock
            blockchainDao.delete(block.bnid)
            sentBlocks.remove(block.parentUuid)
            sendChildBlockFull(childBlockFull)
        }

        //Шаг 6b
        if (container.childFull != null) {
            val childBlockFull = container.childFull
            val bnid = childBlockFull.bnid
            if (!childBlockFull.verification(blocksToDB[bnid]!!.last().otok)) {
                throw Exception("childBlock некорректно подписан")
            }
            blockchainDao.insertAll(blockchainsToDB[bnid]!!)
            for (block in blocksToDB[bnid]!!) {
                blockDao.insertAll(block)
            }
            blockDao.insertAll(childBlockFull)
            blockchainsToDB.remove(bnid)
            blocksToDB.remove(bnid)
        }
    }

    private fun sendAcceptanceBlocks(acceptanceBlocks: AcceptanceBlocks) {
        val payloadContainer = PayloadContainer(blocks = acceptanceBlocks)
        val blockchainJson = serializer.toJson(payloadContainer)
        p2p.send(blockchainJson.encodeToByteArray())
    }

    private fun sendChildBlockFull(childBlock: Block) {
        val payloadContainer = PayloadContainer(childFull = childBlock)
        val blockchainJson = serializer.toJson(payloadContainer)
        p2p.send(blockchainJson.encodeToByteArray())
    }
}