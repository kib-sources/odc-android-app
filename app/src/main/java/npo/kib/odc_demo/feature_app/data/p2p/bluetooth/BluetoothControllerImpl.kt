package npo.kib.odc_demo.feature_app.data.p2p.bluetooth

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import npo.kib.odc_demo.feature_app.data.p2p.bluetooth.BluetoothConnectionStatus.*
import npo.kib.odc_demo.feature_app.domain.model.connection_status.BluetoothConnectionResult
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.BluetoothController
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.CustomBluetoothDevice
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.toCustomBluetoothDevice
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
class BluetoothControllerImpl(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BluetoothController {

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private var dataTransferService: BluetoothDataTransferService? = null

    private val _connectionStatus: MutableStateFlow<BluetoothConnectionStatus> =
        MutableStateFlow(DISCONNECTED)
    private val _connectedDevice: MutableStateFlow<CustomBluetoothDevice?> = MutableStateFlow(null)
    private val _scannedDevices = MutableStateFlow<List<CustomBluetoothDevice>>(emptyList())
    private val _bondedDevices = MutableStateFlow<List<CustomBluetoothDevice>>(emptyList())

    override val bluetoothStateColdFlow: Flow<BluetoothState> = combine(
        _connectionStatus,
        _connectedDevice,
        _scannedDevices,
        _bondedDevices
    ) { status, device, scanned, bonded ->
        BluetoothState(
            connectionStatus = status,
            connectedDevice = device,
            scannedDevices = scanned,
            bondedDevices = bonded
        )
    }

    private val _errors = MutableSharedFlow<String>(extraBufferCapacity = 10)
    override val errors: SharedFlow<String> = _errors.asSharedFlow()

    private val deviceFoundReceiver = DeviceFoundReceiver { device ->
        _scannedDevices.update { devices ->
            val newDevice = device.toCustomBluetoothDevice()
            if (newDevice in devices) devices else devices + newDevice
        }
    }

    private val bluetoothStateReceiver = BluetoothStateReceiver { isConnected, bluetoothDevice ->
        if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == true) {
            _connectionStatus.value = if (isConnected) CONNECTED else DISCONNECTED
        } else {
            CoroutineScope(ioDispatcher).launch {
                _errors.emit("Can't connect to a non-paired device.")
            }
        }
    }

    private var currentServerSocket: BluetoothServerSocket? = null
    private var currentClientSocket: BluetoothSocket? = null

    private lateinit var enableDiscoverableLauncher: ActivityResultLauncher<Intent>

    init {
        updateBondedDevices()
        context.registerReceiver(bluetoothStateReceiver,
            IntentFilter().apply {
                addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            })
    }


    /**
     * As a receiving side, start advertising
     * */
    override fun startAdvertising(
        registry: ActivityResultRegistry,
        duration: Int,
        callback: (Int?) -> Unit
    ) {
        var actualDuration: Int?
        enableDiscoverableLauncher = registry.register(
            "DISCOVERABLE_LAUNCHER",
            ActivityResultContracts.StartActivityForResult()
        ) { //result.resultCode is advertising duration actually
                result ->
            val resultCode = result.resultCode

            Toast.makeText(
                context,
                "Result code = $resultCode",
                Toast.LENGTH_SHORT
            ).show()

            actualDuration = if (resultCode == Activity.RESULT_CANCELED) {
                // The case where the user rejected the system prompt to become discoverable
                Toast.makeText(
                    context,
                    "Rejected",
                    Toast.LENGTH_SHORT
                ).show()
                null
            } else {
                Toast.makeText(
                    context,
                    "started advertising for $resultCode",
                    Toast.LENGTH_SHORT
                ).show()

                resultCode
            }
            callback(actualDuration)
        }

        val discoverableIntent: Intent =
            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(
                    BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                    duration
                )
            }
        enableDiscoverableLauncher.launch(
            discoverableIntent,
            ActivityOptionsCompat.makeBasic()
        )
    }

    /**
     * No other way to cancel advertising other that starting advertising again for 1 second or disabling bluetooth.
     * When bluetooth is enabled back, the phone starts advertising by default for some duration.
     * On some devices the advertising is bugged and always starts for default 120 seconds.
     */ //todo probably should delete this method
    override fun stopAdvertising(registry: ActivityResultRegistry) {
        enableDiscoverableLauncher = registry.register(
            "DISCOVERABLE_LAUNCHER",
            ActivityResultContracts.StartActivityForResult()
        ) {}
        val discoverableIntent: Intent =
            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(
                    BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                    1
                )
            }
        Toast.makeText(
            context,
            "stopAdvertising() invoked from BluetoothController",
            Toast.LENGTH_SHORT
        ).show()
        enableDiscoverableLauncher.launch(
            discoverableIntent,
            ActivityOptionsCompat.makeBasic()
        )
    }

    override fun startDiscovery() {
        context.registerReceiver(
            deviceFoundReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )
        updateBondedDevices()
        bluetoothAdapter?.startDiscovery()
    }

    override fun stopDiscovery() {
        bluetoothAdapter?.cancelDiscovery()
    }


    override fun startBluetoothServerAndGetFlow(): Flow<BluetoothConnectionResult> {
        return flow {
            currentServerSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                "odc_service",
                UUID.fromString(SERVICE_UUID)
            )
            var shouldLoop = true
            while (shouldLoop) {
                currentClientSocket = try {
                    _connectionStatus.value = CONNECTING
                    currentServerSocket?.accept()
                } catch (e: IOException) {
                    shouldLoop = false
                    _errors.emit("Connection acceptance was aborted or timed out")
                    null
                }
                currentClientSocket?.let {
                    currentServerSocket?.close()
                    val otherDevice = currentClientSocket?.remoteDevice?.toCustomBluetoothDevice()
                    setConnectionStatus(CONNECTED)
                    _connectedDevice.value = otherDevice
                    emit(BluetoothConnectionResult.ConnectionEstablished)
                    val service = BluetoothDataTransferService(it)
                    dataTransferService = service

                    emitAll(service.listenForIncomingBytes().map { bytes ->
                        BluetoothConnectionResult.TransferSucceeded(bytes)
                    })
                }
            }
        }.onCompletion {
            closeConnection()
        }.flowOn(ioDispatcher)
    }

    override fun connectToDevice(device: CustomBluetoothDevice): Flow<BluetoothConnectionResult> {
        return flow {
            currentClientSocket = bluetoothAdapter?.getRemoteDevice(device.address)
                ?.createRfcommSocketToServiceRecord(
                    UUID.fromString(SERVICE_UUID)
                )
            stopDiscovery()

            currentClientSocket?.let { socket ->
                try {
                    setConnectionStatus(CONNECTING)
                    socket.connect()
                    setConnectionStatus(CONNECTED)
                    _connectedDevice.value = device
                    emit(BluetoothConnectionResult.ConnectionEstablished)

                    BluetoothDataTransferService(socket).let {
                        dataTransferService = it
                        emitAll(it.listenForIncomingBytes().map { bytes ->
                            BluetoothConnectionResult.TransferSucceeded(bytes = bytes)
                        })
                    }
                } catch (e: IOException) {
                    socket.close()
                    currentClientSocket = null
                    _errors.emit("Connection was interrupted")
                }
            }
        }.onCompletion {
            closeConnection()
        }.flowOn(ioDispatcher)
    }

    override suspend fun trySendBytes(bytes: ByteArray): ByteArray? {
        if (dataTransferService == null) {
            return null
        }
        return withContext(ioDispatcher) {
            val result = dataTransferService?.sendBytes(bytes)
            //if no dataTransferService or send failed with exception return null
            if (result == true) bytes else null
        }
    }

    override fun closeConnection() {
        currentClientSocket?.close()
        currentServerSocket?.close()
        currentClientSocket = null
        currentServerSocket = null
        _connectionStatus.value = DISCONNECTED
        _connectedDevice.value = null
    }

    override fun reset() {
        context.unregisterReceiver(deviceFoundReceiver)
        context.unregisterReceiver(bluetoothStateReceiver)
        enableDiscoverableLauncher.unregister()
        closeConnection()
    }

    private fun updateBondedDevices() {
        bluetoothAdapter?.bondedDevices?.map { it.toCustomBluetoothDevice() }?.also { devices ->
            _bondedDevices.update { devices }
        }
    }

    private fun setConnectionStatus(status: BluetoothConnectionStatus) {
        _connectionStatus.value = status
    }

    //Todo save name to datastore preferences then change to pattern, filter found devices to match pattern,
    // then return the name back when finished
    suspend fun changeMyDeviceName() {
        withContext(ioDispatcher) {/* */ }
    }

    suspend fun changeMyDeviceNameBack() {
        withContext(ioDispatcher) {/* */ }
    }

    companion object {
        const val SERVICE_UUID = "133f71c6-b7b6-437e-8fd1-d2f59cc76066"
    }
}
