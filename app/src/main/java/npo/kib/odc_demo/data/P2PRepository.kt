package npo.kib.odc_demo.data

import android.app.Application
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import npo.kib.odc_demo.core.Wallet
import npo.kib.odc_demo.data.db.BlockchainDatabase
import npo.kib.odc_demo.data.models.*
import npo.kib.odc_demo.data.p2p.ObjectSerializer
import npo.kib.odc_demo.data.p2p.P2PConnection
import java.util.*

class P2PRepository(application: Application) {

    private val p2p = P2PConnection(application)

    // public states

    val connectionResult = p2p.connectionResult
    val searchingStatusFlow = p2p.searchingStatusFlow

    private val _isSendingFlow: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isSendingFlow = _isSendingFlow.asStateFlow()

    private val _requiringStatusFlow = MutableStateFlow(RequiringStatus.NONE)
    val requiringStatusFlow = _requiringStatusFlow.asStateFlow()

    private val _amountRequestFlow: MutableStateFlow<AmountRequest?> = MutableStateFlow(null)
    val amountRequestFlow = _amountRequestFlow.asStateFlow()

    // private fields

    private val serializer = ObjectSerializer()

    private val blockchainDao = BlockchainDatabase.getInstance(application).blockchainDao()
    private val blockDao = BlockchainDatabase.getInstance(application).blockDao()

    private val walletRepository = WalletRepository(application)
    private lateinit var wallet: Wallet

    private val sendingList = LinkedList<BlockchainFromDB>()
    private lateinit var sentBlock: Block

    private lateinit var blockchainToDB: Blockchain
    private lateinit var blocksToDB: List<Block>
    private var receivingAmount: Int = 0

    init {
        var job = Job() as Job
        CoroutineScope(Dispatchers.IO).launch {
            wallet = walletRepository.getOrRegisterWallet()
            connectionResult.collect {
                when (it) {
                    is ConnectingStatus.ConnectionResult -> {
                        if (it.result.status.statusCode != ConnectionsStatusCodes.STATUS_OK) return@collect
                        job = onConnected()
                    }
                    is ConnectingStatus.Disconnected -> {
                        onDisconnected()
                        job.cancel()
                    }
                    else -> job.cancel()
                }
            }
        }
    }

    // public methods

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

    fun requireBanknotes(amount: Int) {
        _requiringStatusFlow.update { RequiringStatus.REQUEST }
        val payloadContainer = PayloadContainer(
            amountRequest = AmountRequest(
                amount = amount,
                userName = walletRepository.userName,
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

    fun sendRejection() {
        p2p.send(serializer.toJson(PayloadContainer()).encodeToByteArray())
        _amountRequestFlow.update { null }
    }

    // private methods

    private fun CoroutineScope.onConnected() = p2p.receivedBytes
        .onEach { bytes -> onBytesReceive(bytes) }
        .launchIn(this)

    private fun onDisconnected() {
        _isSendingFlow.update { null }
        _requiringStatusFlow.update { RequiringStatus.NONE }
        _amountRequestFlow.update { null }
    }

    private suspend fun getBlockchainsByAmount(requiredAmount: Int): ArrayList<BlockchainFromDB> {
        //TODO обработать ситуацию, когда не хватает банкнот для выдачи точной суммы
        var amount = requiredAmount
        val banknoteAmounts = blockchainDao.getBnidsAndAmounts().toCollection(ArrayList())
        banknoteAmounts.sortByDescending { it.amount }

        val blockchainsList = ArrayList<BlockchainFromDB>()
        for (banknoteAmount in banknoteAmounts) {
            if (amount < banknoteAmount.amount) {
                continue
            }

            blockchainsList.add(
                BlockchainFromDB(
                    blockchainDao.getBlockchainByBnid(banknoteAmount.bnid),
                    blockDao.getBlocksByBnid(banknoteAmount.bnid)
                )
            )

            amount -= banknoteAmount.amount
            if (amount <= 0) break
        }

        return blockchainsList
    }

    private fun sendBlockchain(blockchainFromDB: BlockchainFromDB?) {
        if (blockchainFromDB == null) {
            _isSendingFlow.update { false }
            return
        }

        //Создание нового ProtectedBlock
        val newProtectedBlock = wallet.initProtectedBlock(blockchainFromDB.blockchain.protectedBlock)
        blockchainFromDB.blockchain.protectedBlock = newProtectedBlock

        val payloadContainer = PayloadContainer(blockchain = blockchainFromDB)
        val blockchainJson = serializer.toJson(payloadContainer)
        p2p.send(blockchainJson.encodeToByteArray())

        //Запоминаем отправленный parentBlock для последующей верификации
        sentBlock = blockchainFromDB.blocks.last()
    }

    private suspend fun onBytesReceive(bytes: ByteArray) {
        val container = serializer.toObject(bytes.decodeToString())

        if (container.amountRequest != null) {
            onAmountRequest(container.amountRequest)
            return
        }

        //Шаг 2-4.
        if (container.blockchain != null) {
            createAcceptanceBlock(container.blockchain)
            return
        }

        //Шаг 5.
        if (container.blocks != null) {
            onAcceptanceBlocksReceived(container.blocks)
            return
        }

        //Шаг 6b
        if (container.childFull != null) {
            saveAndVerifyNewBlock(container.childFull)
            return
        }

        if (container.amount != null) {
            receivingAmount = container.amount
            return
        }

        _requiringStatusFlow.update { RequiringStatus.REJECT }
    }

    //Шаг 6b
    private fun saveAndVerifyNewBlock(childBlockFull: Block) {
        if (!childBlockFull.verification(blocksToDB.last().otok)) {
            throw Exception("childBlock некорректно подписан")
        }

        blockchainDao.insertAll(blockchainToDB)
        for (block in blocksToDB) {
            blockDao.insertAll(block)
        }
        blockDao.insertAll(childBlockFull)
        receivingAmount -= blockchainToDB.banknote.amount

        if (receivingAmount <= 0) {
            _requiringStatusFlow.update { RequiringStatus.COMPLETED }
        }
    }

    //Шаг 5.
    private suspend fun onAcceptanceBlocksReceived(acceptanceBlocks: AcceptanceBlocks) {
        val childBlockFull = wallet.signature(
            sentBlock,
            acceptanceBlocks.childBlock,
            acceptanceBlocks.protectedBlock
        )
        blockchainDao.delete(acceptanceBlocks.childBlock.bnid)
        sendChildBlockFull(childBlockFull)
        sendBlockchain(sendingList.poll())
    }

    //Шаг 2-4.
    private fun createAcceptanceBlock(blockchainFromDB: BlockchainFromDB) {
        _requiringStatusFlow.update { RequiringStatus.ACCEPTANCE }

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
    }

    private suspend fun onAmountRequest(amountRequest: AmountRequest) {
        val requiredAmount = amountRequest.amount
        val currentAmount = walletRepository.getStoredInWalletSum() ?: 0
        if (requiredAmount <= currentAmount) {
            _amountRequestFlow.update { amountRequest }
        } else {
            sendRejection()
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
