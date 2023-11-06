package npo.kib.odc_demo.feature_app.domain.use_cases

import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
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
import npo.kib.odc_demo.common.core.Wallet
import npo.kib.odc_demo.common.core.models.BanknoteWithProtectedBlock
import npo.kib.odc_demo.common.core.models.Block
import npo.kib.odc_demo.common.util.myLogs
import npo.kib.odc_demo.feature_app.data.p2p.connection_util.ObjectSerializer
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.AmountRequest
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithBlockchain
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.PayloadContainer
import npo.kib.odc_demo.feature_app.domain.model.connection_status.ConnectingStatus
import npo.kib.odc_demo.feature_app.domain.model.connection_status.RequiringStatus
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnection
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import java.util.LinkedList


abstract class P2PBaseUseCase {

    abstract val walletRepository: WalletRepository
    abstract val p2pConnection: P2PConnection

    // public states
    val connectionResult by lazy { p2pConnection.connectionResult }
    val searchingStatusFlow by lazy { p2pConnection.searchingStatusFlow }

    protected val _isSendingFlow = MutableStateFlow<Boolean?>(null)
    val isSendingFlow = _isSendingFlow.asStateFlow()

    protected val _requiringStatusFlow = MutableStateFlow(RequiringStatus.NONE)
    val requiringStatusFlow = _requiringStatusFlow.asStateFlow()

    protected val _amountRequestFlow = MutableStateFlow<AmountRequest?>(null)
    val amountRequestFlow = _amountRequestFlow.asStateFlow()


    protected val serializer = ObjectSerializer()
    private var job = Job() as Job

    protected val banknotesDao by lazy { walletRepository.blockchainDatabase.banknotesDao }
    protected val blockDao by lazy { walletRepository.blockchainDatabase.blockDao }

    protected lateinit var wallet: Wallet

    protected val sendingList = LinkedList<BanknoteWithBlockchain>()
    protected lateinit var sentBlock: Block

    protected lateinit var banknoteToDB: BanknoteWithProtectedBlock
    protected lateinit var blocksToDB: List<Block>
    protected var receivingAmount: Int = 0

    init {
        CoroutineScope(Dispatchers.IO).launch {
            wallet = walletRepository.getOrRegisterWallet()
            connectionResult.collect {
                onConnectionStateChanged(it)
            }
        }
    }

    protected abstract suspend fun onBytesReceive(container: PayloadContainer)

    fun acceptConnection() {
        p2pConnection.acceptConnection()
    }

    fun rejectConnection() {
        p2pConnection.rejectConnection()
    }

    fun sendRejection() {
        p2pConnection.sendBytes(serializer.toCbor(PayloadContainer()))
        _amountRequestFlow.update { null }
    }

    private fun CoroutineScope.onConnectionStateChanged(connectingStatus: ConnectingStatus) {
        when (connectingStatus) {
            is ConnectingStatus.ConnectionResult -> {
                if (connectingStatus.statusCode != ConnectionsStatusCodes.STATUS_OK) return
                job = onConnected()
            }

            is ConnectingStatus.Disconnected -> {
                onDisconnected()
                job.cancel()
            }

            else -> job.cancel()
        }
    }

    private fun CoroutineScope.onConnected() =
        p2pConnection.receivedBytes.map { serializer.toObject(it) }.onEach { myLogs(it) }
            .onEach { bytes -> onBytesReceive(bytes) }.launchIn(this)

    private fun onDisconnected() {
        _isSendingFlow.update { null }
        _requiringStatusFlow.update { RequiringStatus.NONE }
        _amountRequestFlow.update { null }
    }
}