package npo.kib.odc_demo.data

import android.content.Context
import kotlinx.coroutines.flow.update
import npo.kib.odc_demo.data.models.*
import npo.kib.odc_demo.myLogs

class P2pReceiveUseCase(context: Context) : P2pBaseUseCase(context) {

    fun startDiscovery() {
        p2p.startDiscovery()
    }

    fun stopDiscovery() {
        p2p.stopDiscovery()
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

    override suspend fun onBytesReceive(container: PayloadContainer) {
        // Другой юзер хочет перевести нам деньги
        if (container.amount != null) {
            receivingAmount = container.amount
            return
        }

        // Шаги 2-4
        if (container.blockchain != null) {
            acceptance(container.blockchain)
            return
        }

        //  Шаг 6b
        if (container.childFull != null) {
            verifyAndSaveNewBlock(container.childFull)
            return
        }

        _requiringStatusFlow.update { RequiringStatus.REJECT }
    }

    //Шаги 2-4
    private fun acceptance(blockchainFromDB: BlockchainFromDB) {
        _requiringStatusFlow.update { RequiringStatus.ACCEPTANCE }

        val blocks = blockchainFromDB.blocks
        val protectedBlockPart = blockchainFromDB.blockchain.protectedBlock

        //Шаг 4
        val childBlocksPair = wallet.acceptanceInit(blocks, protectedBlockPart)
        val payloadContainer = PayloadContainer(blocks = childBlocksPair)
        val blockchainJson = serializer.toJson(payloadContainer)
        p2p.send(blockchainJson.encodeToByteArray())

        //Шаг 3: Запоминаем блокчейн для добавления в бд в случае успешной верификации
        blockchainToDB = Blockchain(
            bnidKey = blockchainFromDB.blockchain.bnidKey,
            banknote = blockchainFromDB.blockchain.banknote,
            protectedBlock = childBlocksPair.protectedBlock
        )
        blocksToDB = blocks
    }

    //Шаг 6b
    private fun verifyAndSaveNewBlock(childBlockFull: Block) {
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
            myLogs("Дело сделано!")
        }
    }
}