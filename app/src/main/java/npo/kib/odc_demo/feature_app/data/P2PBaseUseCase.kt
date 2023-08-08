package npo.kib.odc_demo.feature_app.data

import android.content.Context
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
import npo.kib.odc_demo.feature_app.data.db.BlockchainDatabase
import npo.kib.odc_demo.feature_app.data.models.AmountRequest
import npo.kib.odc_demo.feature_app.data.models.BanknoteWithBlockchain
import npo.kib.odc_demo.feature_app.data.models.ConnectingStatus
import npo.kib.odc_demo.feature_app.data.models.PayloadContainer
import npo.kib.odc_demo.feature_app.data.models.types.RequiringStatus
import npo.kib.odc_demo.feature_app.data.p2p.P2PConnection
import npo.kib.odc_demo.feature_app.data.p2p.connection_util.ObjectSerializer
import java.util.LinkedList

abstract class P2PBaseUseCase(context: Context) {

    abstract val p2p: P2PConnection

    // public states
    val connectionResult by lazy { p2p.connectionResult }
    val searchingStatusFlow by lazy { p2p.searchingStatusFlow }

    protected val _isSendingFlow = MutableStateFlow<Boolean?>(null)
    val isSendingFlow = _isSendingFlow.asStateFlow()

    protected val _requiringStatusFlow = MutableStateFlow(RequiringStatus.NONE)
    val requiringStatusFlow = _requiringStatusFlow.asStateFlow()

    protected val _amountRequestFlow = MutableStateFlow<AmountRequest?>(null)
    val amountRequestFlow = _amountRequestFlow.asStateFlow()

    // protected fields

    protected val serializer = ObjectSerializer()
    private var job = Job() as Job
    //I think it is better to inject BankRepository in the UseCase constructor and work with it
    protected val banknotesDao = BlockchainDatabase.getInstance(context).banknotesDao()
    protected val blockDao = BlockchainDatabase.getInstance(context).blockDao()

    protected val walletRepository = WalletRepository(context)
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
        p2p.acceptConnection()
    }

    fun rejectConnection() {
        p2p.rejectConnection()
    }

    fun sendRejection() {
        p2p.send(serializer.toCbor(PayloadContainer()))
        _amountRequestFlow.update { null }
    }

    private fun CoroutineScope.onConnectionStateChanged(connectingStatus: ConnectingStatus) {
        when (connectingStatus) {
            is ConnectingStatus.ConnectionResult -> {
                if (connectingStatus.statusCode != ConnectionsStatusCodes.STATUS_OK)
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
        .map { serializer.toObject(it) }
        .onEach { myLogs(it) }
        .onEach { bytes -> onBytesReceive(bytes) }
        .launchIn(this)

    private fun onDisconnected() {
        _isSendingFlow.update { null }
        _requiringStatusFlow.update { RequiringStatus.NONE }
        _amountRequestFlow.update { null }
    }
}
