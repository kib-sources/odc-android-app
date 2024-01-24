package npo.kib.odc_demo.feature_app.domain.use_cases

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.domain.core.Wallet
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithProtectedBlock
import npo.kib.odc_demo.feature_app.domain.util.myLogs
import npo.kib.odc_demo.feature_app.domain.model.serialization.PayloadContainerSerializer.toByteArray
import npo.kib.odc_demo.feature_app.domain.model.serialization.PayloadContainerSerializer.toPayloadContainer
import npo.kib.odc_demo.feature_app.domain.model.connection_status.BluetoothConnectionStatus
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.AmountRequest
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithBlockchain
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.Block
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.legacy.PayloadContainer
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnection
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import java.util.LinkedList


abstract class P2PBaseUseCase {

    protected abstract val walletRepository: WalletRepository
    protected abstract val p2pConnection: P2PConnection


    protected val _amountRequestFlow = MutableStateFlow<AmountRequest?>(null)
    protected val amountRequestFlow = _amountRequestFlow.asStateFlow()


    private var currentJob: Job? = null

    protected lateinit var wallet: Wallet

    protected val sendingList = LinkedList<BanknoteWithBlockchain>()
    protected lateinit var sentBlock: Block
    protected lateinit var banknoteToDB: BanknoteWithProtectedBlock
    protected lateinit var blocksToDB: List<Block>
    protected var receivingAmount: Int = 0

    init {
        CoroutineScope(Dispatchers.IO).launch {
            wallet = walletRepository.getOrRegisterWallet()
//            connectionStatus.collect {
//                onConnectionStateChanged(it)
//            }
        }
    }

    protected abstract suspend fun onBytesReceive(container: PayloadContainer)

    fun acceptConnection() {
        p2pConnection.acceptConnection()
    }

    fun rejectConnection() {
        p2pConnection.rejectConnection()
    }

    suspend fun sendRejection() {
        p2pConnection.sendBytes(PayloadContainer().toByteArray())
        _amountRequestFlow.update { null }
    }

    private fun CoroutineScope.onConnectionStateChanged(connectionStatus: BluetoothConnectionStatus) {
        when (connectionStatus) {
            is BluetoothConnectionStatus.ConnectionEstablished -> {
                currentJob = onConnected()
            }

            is BluetoothConnectionStatus.Disconnected -> {
                onDisconnected()
            }

            else -> resetJob()
        }
    }

    private fun CoroutineScope.onConnected() = p2pConnection.receivedBytes.map { bytes ->
        bytes.toPayloadContainer()
    }.onEach { myLogs(it) }.onEach { bytes ->
        onBytesReceive(bytes)
    }.launchIn(this)


    private fun onDisconnected() {
        resetJob()
    }

    private fun resetJob() {
        currentJob?.cancel()
        currentJob = null
    }
}