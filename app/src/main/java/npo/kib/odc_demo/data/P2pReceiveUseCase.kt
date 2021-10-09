package npo.kib.odc_demo.data

import android.app.Application
import kotlinx.coroutines.flow.update
import npo.kib.odc_demo.data.models.*

class P2pReceiveUseCase(application: Application): P2pBaseUseCase(application) {

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

    override suspend fun onBytesReceive(bytes: ByteArray) {
        val container = serializer.toObject(bytes.decodeToString())

        //Шаг 2-4
        if (container.blockchain != null) {
            createAcceptanceBlock(container.blockchain)
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

    //Шаг 2-4
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

    private fun sendAcceptanceBlocks(acceptanceBlocks: AcceptanceBlocks) {
        val payloadContainer = PayloadContainer(blocks = acceptanceBlocks)
        val blockchainJson = serializer.toJson(payloadContainer)
        p2p.send(blockchainJson.encodeToByteArray())
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
}