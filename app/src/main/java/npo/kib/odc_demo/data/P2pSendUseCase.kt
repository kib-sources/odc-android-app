package npo.kib.odc_demo.data

import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import npo.kib.odc_demo.data.models.*
import npo.kib.odc_demo.data.p2p.P2PConnectionBidirectionalNearbyImpl
import npo.kib.odc_demo.data.p2p.P2pConnectionBidirectional
import java.util.*

open class P2pSendUseCase(
    context: Context,
    override val p2p: P2pConnectionBidirectional = P2PConnectionBidirectionalNearbyImpl(context),
) : P2pBaseUseCase(context) {

    fun startAdvertising() {
        p2p.startAdvertising()
    }

    fun stopAdvertising() {
        p2p.stopAdvertising()
    }

    /**
     * Sending banknotes to another device
     * @param amount Amount to send
     */
    suspend fun sendBanknotes(amount: Int) {
        // Шаг 1
        _isSendingFlow.update { true }
        val blockchainArray = getBlockchainsByAmount(amount)

        for (blockchainFromDB in blockchainArray) {
            sendingList.add(blockchainFromDB)
        }

        // Отправка суммы и первой банкноты
        p2p.send(serializer.toJson(PayloadContainer(amount = amount)).encodeToByteArray())
        sendBlockchain(sendingList.poll())

        for (i in 0 until blockchainArray.size) {
            // Ждем выполнения шагов 2-4
            val bytes = p2p.receivedBytes.take(1).first()
            val container = serializer.toObject(bytes.decodeToString())
            if (container.blocks == null) {
                _requiringStatusFlow.update { RequiringStatus.REJECT }
                return
            }

            //Шаг 5
            onAcceptanceBlocksReceived(container.blocks)
        }
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

    override suspend fun onBytesReceive(container: PayloadContainer) {
        // Случай, когда другой юзер запросил у нас купюры
        if (container.amountRequest != null) {
            onAmountRequest(container.amountRequest)
            return
        }
    }

    //Шаг 1
    private suspend fun onAmountRequest(amountRequest: AmountRequest) {
        val requiredAmount = amountRequest.amount
        val currentAmount = walletRepository.getStoredInWalletSum() ?: 0
        if (requiredAmount <= currentAmount) {
            _amountRequestFlow.update { amountRequest }
        } else {
            sendRejection()
        }
    }

    //Шаг 5
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

    private fun sendChildBlockFull(childBlock: Block) {
        val payloadContainer = PayloadContainer(childFull = childBlock)
        val blockchainJson = serializer.toJson(payloadContainer)
        p2p.send(blockchainJson.encodeToByteArray())
    }
}