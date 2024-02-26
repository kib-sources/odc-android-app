package npo.kib.odc_demo.feature_app.domain.use_cases

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.AcceptanceBlocks
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.Block
import npo.kib.odc_demo.feature_app.domain.model.serialization.PayloadContainerSerializer.toByteArray
import npo.kib.odc_demo.feature_app.domain.model.serialization.PayloadContainerSerializer.toPayloadContainer
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.AmountRequest
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithBlockchain
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.legacy.PayloadContainer
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnection
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository

class P2PSendUseCase(
    override val walletRepository: WalletRepository,
    override val p2pConnection: P2PConnection,
) : P2PBaseUseCase() {

    /**
     * Sending banknotes to another device
     * @param amount Amount to send
     */
    suspend fun sendBanknotes(amount: Int) {
        // Шаг 1

        val blockchainArray = getBanknotesByAmount(amount)

        for (blockchainFromDB in blockchainArray) {
            sendingList.add(blockchainFromDB)
        }

        // Отправка суммы и первой банкноты
        p2pConnection.sendBytes(PayloadContainer(amount = amount).toByteArray())
        sendBanknoteWithBlockchain(sendingList.poll())

        for (i in 0 until blockchainArray.size) {
            // Ждем выполнения шагов 2-4
            val bytes = p2pConnection.receivedBytes.take(1).first()
            val container = bytes.toPayloadContainer()
            if (container.acceptanceBlocks == null) {
                return
            }

            //Шаг 5
            onAcceptanceBlocksReceived(container.acceptanceBlocks)
        }
    }

    override suspend fun onBytesReceive(container: PayloadContainer) = Unit
//    override suspend fun onBytesReceive(container: PayloadContainer) {
//        // Случай, когда другой юзер запросил у нас купюры
//        if (container.amountRequest != null) {
//            onAmountRequest(container.amountRequest)
//            return
//        }
//    }

    /** SSP, NP-hard
     * https://en.wikipedia.org/wiki/Subset_sum_problem
     * */
    private suspend fun getBanknotesByAmount(requiredAmount: Int): ArrayList<BanknoteWithBlockchain> {
        //TODO обработать ситуацию, когда не хватает банкнот для выдачи точной суммы
        var amount = requiredAmount
        val banknoteAmounts = walletRepository.getBanknotesIdsAndAmounts().toCollection(ArrayList())
        banknoteAmounts.sortByDescending {
            it.amount
        }

        val blockchainsList = ArrayList<BanknoteWithBlockchain>()
        for (banknoteAmount in banknoteAmounts) {
            if (amount < banknoteAmount.amount) {
                continue
            }

            blockchainsList.add(
                BanknoteWithBlockchain(
                    walletRepository.getBanknoteByBnid(banknoteAmount.bnid),
                    walletRepository.getBlocksByBnid(banknoteAmount.bnid),
                )
            )

            amount -= banknoteAmount.amount
            if (amount <= 0) break
        }

        return blockchainsList
    }

    private suspend fun sendBanknoteWithBlockchain(banknoteWithBlockchain: BanknoteWithBlockchain?) {
        banknoteWithBlockchain ?: return
        //Создание нового ProtectedBlock
        val newProtectedBlock =
            walletRepository.walletInitProtectedBlock(banknoteWithBlockchain.banknoteWithProtectedBlock.protectedBlock)
        val resultBanknoteWithBlockchain = banknoteWithBlockchain.copy(
            banknoteWithProtectedBlock = banknoteWithBlockchain.banknoteWithProtectedBlock.copy(protectedBlock = newProtectedBlock)
        )
        val payloadContainer = PayloadContainer(banknoteWithBlockchain = resultBanknoteWithBlockchain)
        p2pConnection.sendBytes(payloadContainer.toByteArray())
        //Запоминаем отправленный parentBlock для последующей верификации
        sentBlock = resultBanknoteWithBlockchain.blocks.last()
    }


    //Шаг 1
    private suspend fun onAmountRequest(amountRequest: AmountRequest) {
        val requiredAmount = amountRequest.amount
        val currentAmount = walletRepository.getStoredInWalletSum() ?: 0
        if (requiredAmount <= currentAmount) {
            _amountRequestFlow.update { amountRequest }
        }
        else {
            sendRejection()
        }
    }

    //Шаг 5
    private suspend fun onAcceptanceBlocksReceived(acceptanceBlocks: AcceptanceBlocks) {
//        val childBlockFull = wallet.signature(
//            sentBlock, acceptanceBlocks.childBlock, acceptanceBlocks.protectedBlock
//        )
        val childBlockFull = walletRepository.walletSignature(sentBlock, acceptanceBlocks.childBlock, acceptanceBlocks.protectedBlock)
        walletRepository.deleteBanknoteByBnid(acceptanceBlocks.childBlock.bnid)
        sendChildBlockFull(childBlockFull)
        sendBanknoteWithBlockchain(sendingList.poll())
    }

    private suspend fun sendChildBlockFull(childBlock: Block) {
        val payloadContainer = PayloadContainer(signedBlock = childBlock)
        val blockchainJson = payloadContainer.toByteArray()
        p2pConnection.sendBytes(blockchainJson)
    }
}