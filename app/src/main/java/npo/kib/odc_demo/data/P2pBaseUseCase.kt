package npo.kib.odc_demo.data

import android.app.Application
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import npo.kib.odc_demo.core.Wallet
import npo.kib.odc_demo.data.db.BlockchainDatabase
import npo.kib.odc_demo.data.models.*
import npo.kib.odc_demo.data.p2p.ObjectSerializer
import npo.kib.odc_demo.data.p2p.P2PConnection
import java.util.*

abstract class P2pBaseUseCase(application: Application) {

    protected val p2p = P2PConnection(application)

    // public states

    val connectionResult = p2p.connectionResult
    val searchingStatusFlow = p2p.searchingStatusFlow

    protected val _isSendingFlow: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isSendingFlow = _isSendingFlow.asStateFlow()

    protected val _requiringStatusFlow = MutableStateFlow(RequiringStatus.NONE)
    val requiringStatusFlow = _requiringStatusFlow.asStateFlow()

    protected val _amountRequestFlow: MutableStateFlow<AmountRequest?> = MutableStateFlow(null)
    val amountRequestFlow = _amountRequestFlow.asStateFlow()

    // protected fields

    protected val serializer = ObjectSerializer()
    private var job = Job() as Job

    protected val blockchainDao = BlockchainDatabase.getInstance(application).blockchainDao()
    protected val blockDao = BlockchainDatabase.getInstance(application).blockDao()

    protected val walletRepository = WalletRepository(application)
    protected lateinit var wallet: Wallet

    protected val sendingList = LinkedList<BlockchainFromDB>()
    protected lateinit var sentBlock: Block

    protected lateinit var blockchainToDB: Blockchain
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

    protected abstract suspend fun onBytesReceive(bytes: ByteArray)

    fun acceptConnection() {
        p2p.acceptConnection()
    }

    fun rejectConnection() {
        p2p.rejectConnection()
    }

    fun sendRejection() {
        p2p.send(serializer.toJson(PayloadContainer()).encodeToByteArray())
        _amountRequestFlow.update { null }
    }

    private fun CoroutineScope.onConnectionStateChanged(connectingStatus: ConnectingStatus) {
        when (connectingStatus) {
            is ConnectingStatus.ConnectionResult -> {
                if (connectingStatus.result.status.statusCode != ConnectionsStatusCodes.STATUS_OK)
                    return
                job = onConnected()
            }
            is ConnectingStatus.Disconnected -> {
                onDisconnected()
                job.cancel()
            }
            else -> job.cancel()
        }
    }

    private fun CoroutineScope.onConnected() = p2p.receivedBytes
        .onEach { bytes -> onBytesReceive(bytes) }
        .launchIn(this)

    private fun onDisconnected() {
        _isSendingFlow.update { null }
        _requiringStatusFlow.update { RequiringStatus.NONE }
        _amountRequestFlow.update { null }
    }
}
