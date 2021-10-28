package npo.kib.odc_demo.data

import android.content.Context
import kotlinx.coroutines.flow.update
import npo.kib.odc_demo.core.models.BanknoteWithProtectedBlock
import npo.kib.odc_demo.core.models.Block
import npo.kib.odc_demo.data.models.AmountRequest
import npo.kib.odc_demo.data.models.BanknoteWithBlockchain
import npo.kib.odc_demo.data.models.PayloadContainer
import npo.kib.odc_demo.data.models.RequiringStatus
import npo.kib.odc_demo.data.p2p.P2PConnectionNearbyImpl
import npo.kib.odc_demo.data.p2p.P2pConnection
import npo.kib.odc_demo.data.p2p.P2pConnectionTcpImpl
import npo.kib.odc_demo.myLogs

class P2pReceiveUseCase(
    context: Context,
    override val p2p: P2pConnection = P2PConnectionNearbyImpl(context),
) : P2pBaseUseCase(context) {

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
        if (container.banknoteWithBlockchain != null) {
            acceptance(container.banknoteWithBlockchain)
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
    private fun acceptance(banknoteWithBlockchain: BanknoteWithBlockchain) {
        _requiringStatusFlow.update { RequiringStatus.ACCEPTANCE }

        val blocks = banknoteWithBlockchain.blocks
        val protectedBlockPart = banknoteWithBlockchain.banknoteWithProtectedBlock.protectedBlock

        //Шаг 4
        val childBlocksPair = wallet.acceptanceInit(blocks, protectedBlockPart)
        val payloadContainer = PayloadContainer(blocks = childBlocksPair)
        val blockchainJson = serializer.toJson(payloadContainer)
        p2p.send(blockchainJson.encodeToByteArray())

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
            _requiringStatusFlow.update { RequiringStatus.COMPLETED }
            myLogs("Дело сделано!")
        }
    }
}