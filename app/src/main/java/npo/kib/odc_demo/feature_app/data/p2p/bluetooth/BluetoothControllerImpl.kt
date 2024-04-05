package npo.kib.odc_demo.feature_app.data.p2p.bluetooth

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import npo.kib.odc_demo.feature_app.data.p2p.bluetooth.BluetoothConnectionStatus.*
import npo.kib.odc_demo.feature_app.domain.model.connection_status.BluetoothConnectionResult
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.BluetoothController
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.BluetoothController.Companion.DEVICE_NAME_PREFIX
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.BluetoothController.Companion.SERVICE_UUID
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.CustomBluetoothDevice
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.toCustomBluetoothDevice
import npo.kib.odc_demo.feature_app.domain.util.containsPrefix
import npo.kib.odc_demo.feature_app.domain.util.log
import npo.kib.odc_demo.feature_app.domain.util.withoutPrefix
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
class BluetoothControllerImpl(
    private val context: Context, private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
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
        _connectionStatus, _connectedDevice, _scannedDevices, _bondedDevices
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
        var newDevice = device.toCustomBluetoothDevice()
        this@BluetoothControllerImpl.log("Found a device! : $newDevice")
        if (newDevice.name.containsPrefix(DEVICE_NAME_PREFIX)) {
            val newName =
                newDevice.name!!.withoutPrefix(DEVICE_NAME_PREFIX).ifBlank { "Blank name" }
            newDevice = newDevice.copy(name = newName)
            val scannedMap = _scannedDevices.value.associate { it.address to it.name }
            //if device with the same address is already present, replace the name, else add the new device
            val deviceIndex = scannedMap.keys.indexOf(newDevice.address)
            _scannedDevices.update { devices ->
                if (deviceIndex == -1) (devices + newDevice).sortedByDescending { it.name } else {
                    devices.toMutableList().apply {
                        this[deviceIndex] = newDevice
                        sortedByDescending { it.name }
                    }
                }
            }
        }
    }

    private val bluetoothStateReceiver = BluetoothStateReceiver { isConnected, bluetoothDevice ->
        if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == true) {
            setConnectionStatus(if (isConnected) CONNECTED else DISCONNECTED)
        } else {
            CoroutineScope(ioDispatcher).launch {
                _errors.emit("Pair devices first before connecting")
            }
        }
    }

    private var currentServerSocket: BluetoothServerSocket? = null
    private var currentClientSocket: BluetoothSocket? = null

    private lateinit var enableDiscoverableLauncher: ActivityResultLauncher<Intent>

    init {
        updateBondedDevices()
        context.registerReceiver(bluetoothStateReceiver, IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        })
    }


    /**
     * As a receiving side, start advertising
     * */
    override fun startAdvertising(
        registry: ActivityResultRegistry, duration: Int, callback: (Int?) -> Unit
    ) {
        if (_connectionStatus.value != DISCONNECTED) {
            log("Tried advertising when not in disconnected state.")
            return
        }

        var actualDuration: Int?
        enableDiscoverableLauncher = registry.register(
            "DISCOVERABLE_LAUNCHER", ActivityResultContracts.StartActivityForResult()
        ) { //result.resultCode is advertising duration actually
                result ->
            val resultCode = result.resultCode

            actualDuration = if (resultCode == Activity.RESULT_CANCELED) {
                // The case where the user rejected the system prompt to become discoverable
                this.log("Rejected advertising")
                null
            } else {
                resultCode
            }

            callback(actualDuration)
            actualDuration?.let {
                this.log("Started advertising for: $actualDuration")
                setConnectionStatus(ADVERTISING)
            }
        }

        val discoverableIntent: Intent =
            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration)
            }
        enableDiscoverableLauncher.launch(discoverableIntent, ActivityOptionsCompat.makeBasic())
    }

    /**
     * No other way to cancel advertising other that starting advertising again for 1 second or disabling bluetooth.
     * When bluetooth is enabled back, the phone starts advertising by default for some duration.
     * On some devices the advertising is bugged and always starts for default 120 seconds.
     */ //todo probably should delete this method
    override fun stopAdvertising(registry: ActivityResultRegistry) {
        if (_connectionStatus.value != ADVERTISING) {
            this.log("Tried to stop advertising when not advertising.")
            return
        }
        this.log("Stopping advertising")
        enableDiscoverableLauncher = registry.register(
            "DISCOVERABLE_LAUNCHER", ActivityResultContracts.StartActivityForResult()
        ) {}
        val discoverableIntent: Intent =
            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1)
            }
        enableDiscoverableLauncher.launch(discoverableIntent, ActivityOptionsCompat.makeBasic())
    }

    override fun startDiscovery() {
        if (_connectionStatus.value != DISCONNECTED) {
            this.log("Tried discovering when not in disconnected state.")
            return
        }
        context.registerReceiver(deviceFoundReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        updateBondedDevices()
        bluetoothAdapter?.startDiscovery()
        setConnectionStatus(DISCOVERING)
    }

    override fun stopDiscovery() {
        if (_connectionStatus.value != DISCOVERING) {
            this.log("Tried to stop discovering when not discovering.")
            return
        }
        bluetoothAdapter?.cancelDiscovery()
        setConnectionStatus(DISCONNECTED)
    }


    override fun startBluetoothServerAndGetFlow(): Flow<BluetoothConnectionResult> {
        return flow {
            currentServerSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                "odc_service", UUID.fromString(SERVICE_UUID)
            )
            var shouldLoop = true
            while (shouldLoop) {
                currentClientSocket = try {
                    setConnectionStatus(CONNECTING)
                    currentServerSocket?.accept()
                } catch (e: IOException) {
                    shouldLoop = false
                    this@BluetoothControllerImpl.log("Connection acceptance was aborted or timed out: $e")
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
                    try {
                        emitAll(service.listenForIncomingBytes().map { bytes ->
                            BluetoothConnectionResult.TransferSucceeded(bytes)
                        })
                    } catch (e: BluetoothDataTransferService.TransferFailedException) {
                        _errors.emit(e.toString())
                        throw CancellationException("Caught $e")
                    }
                }
            }
        }.onCompletion {
            this@BluetoothControllerImpl.log("startBluetoothServerAndGetFlow flow.onCompletion{} called")
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
                        try {
                            emitAll(it.listenForIncomingBytes().map { bytes ->
                                BluetoothConnectionResult.TransferSucceeded(bytes = bytes)
                            })
                        } catch (e: BluetoothDataTransferService.TransferFailedException) {
                            _errors.emit(e.toString())
                            throw CancellationException("Caught $e")
                        }

                    }
                } catch (e: IOException) {
                    socket.close()
                    currentClientSocket = null
                    this@BluetoothControllerImpl.log("connectToDevice(): Connection interrupted: $e")
                    _errors.emit("Connection was interrupted")
                }
            }
        }.onCompletion {
            this@BluetoothControllerImpl.log("connectToDevice flow.onCompletion{} called")
            closeConnection()
        }.flowOn(ioDispatcher)
    }

    override suspend fun trySendBytes(bytes: ByteArray): ByteArray? {
        this.log("trySendBytes() called")
        if (dataTransferService == null) {
            this.log("trySendBytes(): DataTransferService is null")
            return null
        }
        return withContext(ioDispatcher) {
            val result = dataTransferService?.sendBytes(bytes)
            this@BluetoothControllerImpl.log("trySendBytes result: $result")
            //if no dataTransferService or send failed with exception return null
            if (result == true) bytes else null
        }
    }

    override fun closeConnection() {
        this.log("closeConnection() called")
        currentClientSocket?.close()
        currentServerSocket?.close()
        currentClientSocket = null
        currentServerSocket = null
        setConnectionStatus(DISCONNECTED)
        _connectedDevice.value = null
        this.log("closeConnection() finished")
    }

    override fun reset() {
        this.log("reset() called")
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

    override suspend fun setDeviceName(newName: String): Boolean {
        this.log("setDeviceName($newName) called")
        return withContext(ioDispatcher) {
            bluetoothAdapter?.setName(newName) ?: false
        }
    }

    override fun getDeviceName(): String {
        this.log("getDeviceName() called")
        return bluetoothAdapter?.name ?: "null"
    }
}