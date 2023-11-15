package npo.kib.odc_demo.feature_app.domain.use_cases

import npo.kib.odc_demo.common.core.models.BanknoteWithProtectedBlock
import npo.kib.odc_demo.common.core.models.Block
import npo.kib.odc_demo.common.util.myLogs
import npo.kib.odc_demo.feature_app.data.p2p.connection_util.ObjectSerializer.toByteArray
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithBlockchain
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.PayloadContainer
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnection
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository

class P2PReceiveUseCase(
    override val walletRepository: WalletRepository,
    override val p2pConnection: P2PConnection
) : P2PBaseUseCase() {


    fun startAdvertising() {
        p2pConnection.startAdvertising()
    }

    fun stopAdvertising() {
        p2pConnection.stopAdvertising()
    }

//    suspend fun requireBanknotes(amount: Int) {
//        _requiringStatusFlow.update { RequiringStatus.REQUEST }
//        val payloadContainer = PayloadContainer(
//            amountRequest = AmountRequest(
//                amount = amount, userName = walletRepository.userName, wid = wallet.wid
//            )
//        )
//        val amountJson = serializer.toCbor(payloadContainer)
//        p2pConnection.sendBytes(amountJson)
//    }

    override suspend fun onBytesReceive(container: PayloadContainer) {
        // Другой юзер хочет перевести нам деньги
        if (container.amount != null) {
            receivingAmount = container.amount
            return
        }

        // Шаги 2-4
        if (container.banknoteWithBlockchain != null) {
            acceptance(container.banknoteWithBlockchain)
            return
        }

        //  Шаг 6b
        if (container.childFull != null) {
            verifyAndSaveNewBlock(container.childFull)
            return
        }

    }

    //Шаги 2-4
    private suspend fun acceptance(banknoteWithBlockchain: BanknoteWithBlockchain) {
//        _requiringStatusFlow.update { RequiringStatus.ACCEPTED }

        val blocks = banknoteWithBlockchain.blocks
        val protectedBlockPart = banknoteWithBlockchain.banknoteWithProtectedBlock.protectedBlock

        //Шаг 4
        val childBlocksPair = wallet.acceptanceInit(blocks, protectedBlockPart)
        val payloadContainer = PayloadContainer(blocks = childBlocksPair)
        val blockchainJson = payloadContainer.toByteArray()
        p2pConnection.sendBytes(blockchainJson)

        //Шаг 3: Запоминаем блокчейн для добавления в бд в случае успешной верификации
        banknoteToDB = BanknoteWithProtectedBlock(
            banknote = banknoteWithBlockchain.banknoteWithProtectedBlock.banknote,
            protectedBlock = childBlocksPair.protectedBlock
        )
        blocksToDB = blocks
    }

    //Шаг 6b
    private fun verifyAndSaveNewBlock(childBlockFull: Block) {
        // TODO verification disabled for demo
//        if (!childBlockFull.verification(blocksToDB.last().otok)) {
//            throw Exception("childBlock некорректно подписан")
//        }

        banknotesDao.insert(banknoteToDB)
        blocksToDB.forEach { block -> blockDao.insert(block) }
        blockDao.insert(childBlockFull)

        receivingAmount -= banknoteToDB.banknote.amount
        if (receivingAmount <= 0) {
//            _requiringStatusFlow.update { RequiringStatus.COMPLETED }
            myLogs("Операция завершена успешно!")
        }
    }

}