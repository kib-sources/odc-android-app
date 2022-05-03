package npo.kib.odc_demo.data

import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import npo.kib.odc_demo.core.models.AcceptanceBlocks
import npo.kib.odc_demo.core.models.Block
import npo.kib.odc_demo.data.models.AmountRequest
import npo.kib.odc_demo.data.models.BanknoteWithBlockchain
import npo.kib.odc_demo.data.models.PayloadContainer
import npo.kib.odc_demo.data.models.RequiringStatus
import npo.kib.odc_demo.data.p2p.P2PConnectionNearbyImpl
import npo.kib.odc_demo.data.p2p.P2pConnectionBidirectional
import java.util.*

open class P2pSendUseCase(
    context: Context,
    override val p2p: P2pConnectionBidirectional = P2PConnectionNearbyImpl(context),
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
        val blockchainArray = getBanknotesByAmount(amount)

        for (blockchainFromDB in blockchainArray) {
            sendingList.add(blockchainFromDB)
        }

        // Отправка суммы и первой банкноты
        p2p.send(serializer.toCbor(PayloadContainer(amount = amount)))
        sendBanknoteWithBlockchain(sendingList.poll())

        for (i in 0 until blockchainArray.size) {
            // Ждем выполнения шагов 2-4
            val bytes = p2p.receivedBytes.take(1).first()
            val container = serializer.toObject(bytes)
            if (container.blocks == null) {
                _requiringStatusFlow.update { RequiringStatus.REJECT }
                return
            }

            //Шаг 5
            onAcceptanceBlocksReceived(container.blocks)
        }
    }

    private suspend fun getBanknotesByAmount(requiredAmount: Int): ArrayList<BanknoteWithBlockchain> {
        //TODO обработать ситуацию, когда не хватает банкнот для выдачи точной суммы
        var amount = requiredAmount
        val banknoteAmounts = banknotesDao.getBnidsAndAmounts().toCollection(ArrayList())
        banknoteAmounts.sortByDescending { it.amount }

        val blockchainsList = ArrayList<BanknoteWithBlockchain>()
        for (banknoteAmount in banknoteAmounts) {
            if (amount < banknoteAmount.amount) {
                continue
            }

            blockchainsList.add(
                BanknoteWithBlockchain(
                    banknotesDao.getBlockchainByBnid(banknoteAmount.bnid),
                    blockDao.getBlocksByBnid(banknoteAmount.bnid)
                )
            )

            amount -= banknoteAmount.amount
            if (amount <= 0) break
        }

        return blockchainsList
    }

    private fun sendBanknoteWithBlockchain(banknoteWithBlockchain: BanknoteWithBlockchain?) {
        if (banknoteWithBlockchain == null) {
            _isSendingFlow.update { false }
            return
        }

        //Создание нового ProtectedBlock
        val newProtectedBlock =
            wallet.initProtectedBlock(banknoteWithBlockchain.banknoteWithProtectedBlock.protectedBlock)
        banknoteWithBlockchain.banknoteWithProtectedBlock.protectedBlock = newProtectedBlock

        val payloadContainer = PayloadContainer(banknoteWithBlockchain = banknoteWithBlockchain)
        val blockchainJson = serializer.toCbor(payloadContainer)
        p2p.send(blockchainJson)

        //Запоминаем отправленный parentBlock для последующей верификации
        sentBlock = banknoteWithBlockchain.blocks.last()
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
        banknotesDao.deleteByBnid(acceptanceBlocks.childBlock.bnid)
        sendChildBlockFull(childBlockFull)
        sendBanknoteWithBlockchain(sendingList.poll())
    }

    private fun sendChildBlockFull(childBlock: Block) {
        val payloadContainer = PayloadContainer(childFull = childBlock)
        val blockchainJson = serializer.toCbor(payloadContainer)
        p2p.send(blockchainJson)
    }
}