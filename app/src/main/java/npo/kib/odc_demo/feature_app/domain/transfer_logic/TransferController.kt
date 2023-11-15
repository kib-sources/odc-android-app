package npo.kib.odc_demo.feature_app.domain.transfer_logic

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import npo.kib.odc_demo.common.core.models.Block
import npo.kib.odc_demo.feature_app.domain.model.DataPacket
import npo.kib.odc_demo.feature_app.domain.model.serialization.BytesToTypeConverter.deserializeToObject
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.AmountRequest
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithBlockchain
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknotesList
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.DataPacketType
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.TransactionResult
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.UserInfo
import npo.kib.odc_demo.feature_app.domain.repository.BankRepository

abstract class TransferController(val bankRepository: BankRepository) {

    abstract suspend fun acceptance(banknoteWithBlockchain: BanknoteWithBlockchain)

    abstract suspend fun verifyAndSaveNewBlock(childBlockFull: Block)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val currentDataPacketTypeChannel: Channel<DataPacketType> =
        Channel(capacity = UNLIMITED)
    val currentDataPacketTypeFlow: Flow<DataPacketType> =
        currentDataPacketTypeChannel.receiveAsFlow()


    private val userInfoChannel: Channel<UserInfo> = Channel(capacity = UNLIMITED)
    val userInfoFlow: Flow<UserInfo> = userInfoChannel.receiveAsFlow()

    private val amountRequestChannel: Channel<AmountRequest> = Channel(capacity = UNLIMITED)
    val amountRequestFlow: Flow<AmountRequest> = amountRequestChannel.receiveAsFlow()

    private val banknotesChannel: Channel<BanknotesList> = Channel(capacity = UNLIMITED)
    val banknotesFlow: Flow<BanknotesList> = banknotesChannel.receiveAsFlow()

    private val unsignedBlockChannel: Channel<Block> = Channel(capacity = UNLIMITED)
    val unsignedBlockFlow: Flow<Block> = unsignedBlockChannel.receiveAsFlow()

    private val signedBlockChannel: Channel<Block> = Channel(capacity = UNLIMITED)
    val signedBlockFlow: Flow<Block> = signedBlockChannel.receiveAsFlow()

    private val resultChannel: Channel<TransactionResult> = Channel(capacity = UNLIMITED)
    val resultFlow: Flow<TransactionResult> = resultChannel.receiveAsFlow()

    suspend fun processDataPacket(dataPacket: DataPacket) {
        val packetType = dataPacket.packetType
        val packetBytes = dataPacket.bytes
        currentDataPacketTypeChannel.send(packetType)
        with(packetBytes) {
            when (packetType) {
                DataPacketType.USER_INFO -> userInfoChannel.send(deserializeToObject(UserInfo::class) as UserInfo)

                DataPacketType.AMOUNT -> amountRequestChannel.send(deserializeToObject(AmountRequest::class) as AmountRequest)

                DataPacketType.BANKNOTES -> banknotesChannel.send(deserializeToObject(BanknotesList::class) as BanknotesList)

                DataPacketType.UNSIGNED_BLOCK -> unsignedBlockChannel.send(deserializeToObject(Block::class) as Block)

                DataPacketType.SIGNED_BLOCK -> signedBlockChannel.send(deserializeToObject(Block::class) as Block)

                DataPacketType.RESULT -> resultChannel.send(deserializeToObject(TransactionResult::class) as TransactionResult)

            }
        }
    }

}