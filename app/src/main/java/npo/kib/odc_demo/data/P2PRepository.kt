package npo.kib.odc_demo.data

import android.app.Application
import androidx.preference.PreferenceManager
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import npo.kib.odc_demo.R
import npo.kib.odc_demo.core.Wallet
import npo.kib.odc_demo.data.db.BlockchainDatabase
import npo.kib.odc_demo.data.models.*
import npo.kib.odc_demo.data.p2p.ObjectSerializer
import npo.kib.odc_demo.data.p2p.P2PConnection
import java.util.*

class P2PRepository(application: Application) {
    private val p2p = P2PConnection(application)
    private val serializer = ObjectSerializer()

    val connectionResult = p2p.connectionResult
    val searchingStatusFlow = p2p.searchingStatusFlow

    private val db = BlockchainDatabase.getInstance(application)
    private val blockchainDao = db.blockchainDao()
    private val blockDao = db.blockDao()

    private val walletRepository = WalletRepository(application)
    private lateinit var wallet: Wallet

    private val _isSendingFlow: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isSendingFlow = _isSendingFlow.asStateFlow()

    private val _requiringStatusFlow: MutableStateFlow<RequiringStatus> = MutableStateFlow(
        RequiringStatus.NONE
    )
    val requiringStatusFlow = _requiringStatusFlow.asStateFlow()

    private val _amountRequestFlow: MutableStateFlow<AmountRequest?> = MutableStateFlow(null)
    val amountRequestFlow = _amountRequestFlow.asStateFlow()

    private val usernameKey = application.resources.getString(R.string.username_key)
    private val defaultUsername = application.resources.getString(R.string.default_username)
    private val prefs = PreferenceManager.getDefaultSharedPreferences(application)
    private val userName = prefs.getString(usernameKey, defaultUsername) ?: defaultUsername

    private val sendingList = LinkedList<BlockchainFromDB>()
    private lateinit var sentBlock: Block

    private lateinit var blockchainToDB: Blockchain
    private lateinit var blocksToDB: List<Block>
    private var receivingAmount: Int = 0

    init {
        var job = Job() as Job
        CoroutineScope(Dispatchers.IO).launch {
            wallet = walletRepository.getWallet()
            connectionResult.collect {
                when (it) {
                    is ConnectingStatus.ConnectionResult -> {
                        if (it.result.status.statusCode == ConnectionsStatusCodes.STATUS_OK) {
                            job =
                                p2p.receivedBytes.onEach { bytes -> receiveBytes(bytes) }
                                    .launchIn(this)
                        }
                    }
                    is ConnectingStatus.Disconnected -> {
                        _isSendingFlow.update { null }
                        _requiringStatusFlow.update { RequiringStatus.NONE }
                        _amountRequestFlow.update { null }
                        job.cancel()
                    }
                    else -> job.cancel()
                }
            }
        }
    }

    fun startAdvertising() {
        p2p.startAdvertising()
    }

    fun startDiscovery() {
        p2p.startDiscovery()
    }

    fun stopAdvertising() {
        p2p.stopAdvertising()
    }

    fun stopDiscovery() {
        p2p.stopDiscovery()
    }

    fun acceptConnection() {
        p2p.acceptConnection()
    }

    fun rejectConnection() {
        p2p.rejectConnection()
    }

    suspend fun getSum() = blockchainDao.getSum()

    private suspend fun getBlockchainsByAmount(requiredAmount: Int): ArrayList<BlockchainFromDB> {
        //TODO обработать ситуацию, когда не хватает банкнот для выдачи точной суммы
        var amount = requiredAmount
        val banknoteAmounts = blockchainDao.getBnidsAndAmounts().toCollection(ArrayList())
        banknoteAmounts.sortByDescending { it.amount }
        val blockchainsList = ArrayList<BlockchainFromDB>()
        for (banknoteAmount in banknoteAmounts) {
            if (amount >= banknoteAmount.amount) {
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

    fun requireBanknotes(amount: Int) {
        _requiringStatusFlow.update { RequiringStatus.REQUEST }
        val payloadContainer = PayloadContainer(
            amountRequest = AmountRequest(
                amount = amount,
                userName = userName,
                wid = wallet.wid
            )
        )
        val amountJson = serializer.toJson(payloadContainer)
        p2p.send(amountJson.encodeToByteArray())
    }

    /**
     * Sending banknotes to another device
     * @param amount Amount to send
     */
    suspend fun sendBanknotes(amount: Int) {
        //Шаг 1.
        _isSendingFlow.update { true }
        val blockchainArray = getBlockchainsByAmount(amount)

        for (blockchainFromDB in blockchainArray) {
            sendingList.add(blockchainFromDB)
        }
        //Отправка суммы и первой банкноты
        p2p.send(serializer.toJson(PayloadContainer(amount = amount)).encodeToByteArray())
        sendBlockchain(sendingList.poll())
    }

    private fun sendBlockchain(blockchainFromDB: BlockchainFromDB?) {
        if (blockchainFromDB == null) {
            _isSendingFlow.update { false }
        } else {
            //Создание нового ProtectedBlock
            val newProtectedBlock =
                wallet.initProtectedBlock(blockchainFromDB.blockchain.protectedBlock)
            blockchainFromDB.blockchain.protectedBlock = newProtectedBlock

            val payloadContainer = PayloadContainer(blockchain = blockchainFromDB)
            val blockchainJson = serializer.toJson(payloadContainer)
            p2p.send(blockchainJson.encodeToByteArray())
            //Запоминаем отправленный parentBlock для последующей верификации
            sentBlock = blockchainFromDB.blocks.last()
        }
    }

    private suspend fun receiveBytes(bytes: ByteArray) {
        val container = serializer.toObject(bytes.decodeToString())

        if (container.amountRequest != null) {
            val requiredAmount = container.amountRequest.amount
            val currentAmount = getSum() ?: 0
            if (requiredAmount <= currentAmount) {
                _amountRequestFlow.update { container.amountRequest }
            } else sendRejection()
        } else

            if (container.blockchain != null) {
                _requiringStatusFlow.update { RequiringStatus.ACCEPTANCE }
                val blockchainFromDB = container.blockchain

                //Шаг 2-4.
                val blocks = blockchainFromDB.blocks
                val protectedBlockPart = blockchainFromDB.blockchain.protectedBlock

                val childBlocksPair = wallet.acceptanceInit(blocks, protectedBlockPart)
                sendAcceptanceBlocks(childBlocksPair)

                //Запоминаем блокчейн для добавления в бд в случае успешной верификации
                blockchainToDB = Blockchain(
                    bnidKey = blockchainFromDB.blockchain.bnidKey,
                    banknote = blockchainFromDB.blockchain.banknote,
                    protectedBlock = childBlocksPair.protectedBlock
                )
                blocksToDB = blocks
            } else

            //Шаг 5.
                if (container.blocks != null) {
                    val acceptanceBlocks = container.blocks
                    val childBlockFull = wallet.signature(
                        sentBlock,
                        acceptanceBlocks.childBlock,
                        acceptanceBlocks.protectedBlock
                    )
                    blockchainDao.delete(acceptanceBlocks.childBlock.bnid)
                    sendChildBlockFull(childBlockFull)
                    sendBlockchain(sendingList.poll())
                } else

                //Шаг 6b
                    if (container.childFull != null) {
                        val childBlockFull = container.childFull
                        if (!childBlockFull.verification(blocksToDB.last().otok)) {
                            throw Exception("childBlock некорректно подписан")
                        }
                        blockchainDao.insertAll(blockchainToDB)
                        for (block in blocksToDB) {
                            blockDao.insertAll(block)
                        }
                        blockDao.insertAll(childBlockFull)
                        receivingAmount -= blockchainToDB.banknote.amount
                        if (receivingAmount <= 0) _requiringStatusFlow.update { RequiringStatus.COMPLETED }
                    } else

                        if (container.amount != null) {
                            receivingAmount = container.amount
                        } else {
                            _requiringStatusFlow.update { RequiringStatus.REJECT }
                        }
    }

    fun sendRejection() {
        p2p.send(serializer.toJson(PayloadContainer()).encodeToByteArray())
        _amountRequestFlow.update { null }
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