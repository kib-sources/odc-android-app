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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.domain.model.connection_status.BluetoothConnectionStatus
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.BluetoothController
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.CustomBluetoothDevice
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.toCustomBluetoothDevice
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
class BluetoothControllerImpl(
    private val context: Context
) : BluetoothController {

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private var dataTransferService: BluetoothDataTransferService? = null

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _scannedDevices = MutableStateFlow<List<CustomBluetoothDevice>>(emptyList())
    override val scannedDevices: StateFlow<List<CustomBluetoothDevice>> = _scannedDevices.asStateFlow()

    private val _bondedDevices = MutableStateFlow<List<CustomBluetoothDevice>>(emptyList())
    override val bondedDevices: StateFlow<List<CustomBluetoothDevice>> = _bondedDevices.asStateFlow()

    private val _errors = MutableSharedFlow<String>()
    override val errors: SharedFlow<String> = _errors.asSharedFlow()

// not needed here, it is higher in hierarchy
//    private val _receivedBytes = Channel<ByteArray>(capacity = Channel.UNLIMITED)
//    val receivedBytes : Flow<ByteArray> = _receivedBytes.receiveAsFlow()


    private val deviceFoundReceiver = DeviceFoundReceiver { device ->
        _scannedDevices.update { devices ->
            val newDevice = device.toCustomBluetoothDevice()
            if (newDevice in devices) devices else devices + newDevice
        }
    }

    private val bluetoothStateReceiver = BluetoothStateReceiver { isConnected, bluetoothDevice ->
        if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == true) {
            _isConnected.update { isConnected }
        }
        else {
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit("Can't connect to a non-paired device.")
            }
        }
    }



    private var currentServerSocket: BluetoothServerSocket? = null
    private var currentClientSocket: BluetoothSocket? = null


    private lateinit var enableDiscoverableLauncher: ActivityResultLauncher<Intent>

    /**
     * As a receiving side, start advertising
     * */
    override fun startAdvertising(
        registry: ActivityResultRegistry, duration: Int, callback: (Int?) -> Unit
    ) {
        var actualDuration: Int?
        enableDiscoverableLauncher = registry.register(
            "DISCOVERABLE_LAUNCHER", ActivityResultContracts.StartActivityForResult()
        ) { //result.resultCode is advertising duration actually
                result ->
            val resultCode = result.resultCode

            Toast.makeText(context, "Result code = $resultCode", Toast.LENGTH_SHORT).show()

            actualDuration = if (resultCode == Activity.RESULT_CANCELED) {
                // The case where the user rejected the system prompt to become discoverable
                Toast.makeText(context, "Rejected", Toast.LENGTH_SHORT).show()
                null
            }
            else {
                Toast.makeText(context, "started advertising for $resultCode", Toast.LENGTH_SHORT)
                    .show()
                resultCode
            }
            callback(actualDuration)
        }

        val discoverableIntent: Intent =
            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration)
            }
        enableDiscoverableLauncher.launch(discoverableIntent, ActivityOptionsCompat.makeBasic())
    }

    /**
     * Start advertising for 1 second, no other way to cancel advertising (except disabling bluetooth).
     * When bluetooth is enabled back the phone starts advertising by default for some duration.
     * On some devices the advertising is bugged and always starts for default 120 seconds.
     */
    override fun stopAdvertising(registry: ActivityResultRegistry) {
        enableDiscoverableLauncher = registry.register(
            "DISCOVERABLE_LAUNCHER", ActivityResultContracts.StartActivityForResult()
        ) {}
        val discoverableIntent: Intent =
            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1)
            }
        Toast.makeText(
            context, "stopAdvertising() invoked from BluetoothController", Toast.LENGTH_SHORT
        ).show()
        enableDiscoverableLauncher.launch(discoverableIntent, ActivityOptionsCompat.makeBasic())
    }

    override fun startDiscovery() {
        context.registerReceiver(
            deviceFoundReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND)
        )
        updateBondedDevices()
        bluetoothAdapter?.startDiscovery()
    }

    override fun stopDiscovery() {
        bluetoothAdapter?.cancelDiscovery()
    }


    override fun startBluetoothServerAndGetFlow(): Flow<BluetoothConnectionStatus> {
        return flow {
            currentServerSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                "odc_service", UUID.fromString(SERVICE_UUID)
            )
            var shouldLoop = true
            while (shouldLoop) {
                currentClientSocket = try {
                    currentServerSocket?.accept()
                } catch (e: IOException) {
                    shouldLoop = false
                    null
                }
                currentClientSocket?.let {
                    currentServerSocket?.close()
                    emit(
                        BluetoothConnectionStatus.ConnectionEstablished(
                            withDevice = currentClientSocket?.remoteDevice?.toCustomBluetoothDevice()
                        )
                    )
                    val service = BluetoothDataTransferService(it)
                    dataTransferService = service

                    emitAll(service.listenForIncomingBytes().map { bytes ->
                        BluetoothConnectionStatus.TransferSucceeded(bytes)
                    })
                }
            }
        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    override fun connectToDevice(device: CustomBluetoothDevice): Flow<BluetoothConnectionStatus> {
        return flow {
            currentClientSocket = bluetoothAdapter?.getRemoteDevice(device.address)
                ?.createRfcommSocketToServiceRecord(
                    UUID.fromString(SERVICE_UUID)
                )
            stopDiscovery()

            currentClientSocket?.let { socket ->
                try {
                    socket.connect()
                    emit(BluetoothConnectionStatus.ConnectionEstablished(withDevice = device))

                    BluetoothDataTransferService(socket).let {
                        dataTransferService = it
                        emitAll(it.listenForIncomingBytes().map { bytes ->
                            BluetoothConnectionStatus.TransferSucceeded(bytes = bytes)
                        })
                    }
                } catch (e: IOException) {
                    socket.close()
                    currentClientSocket = null
                    emit(BluetoothConnectionStatus.Error("Connection was interrupted"))
                }
            }
        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun trySendBytes(bytes: ByteArray): ByteArray? {
        if (dataTransferService == null) {
            return null
        }
        val result = dataTransferService?.sendBytes(bytes)
        //if no dataTransferService or send failed with exception return null
        return if (result == true) bytes else null
    }

    override fun closeConnection() {
        currentClientSocket?.close()
        currentServerSocket?.close()
        currentClientSocket = null
        currentServerSocket = null
    }

    override fun reset() {
        context.unregisterReceiver(deviceFoundReceiver)
        context.unregisterReceiver(bluetoothStateReceiver)
        closeConnection()
        enableDiscoverableLauncher.unregister()
    }

    private fun updateBondedDevices() {
        bluetoothAdapter?.bondedDevices?.map { it.toCustomBluetoothDevice() }?.also { devices ->
            _bondedDevices.update { devices }
        }
    }

    //Todo save name to datastore preferences then change to pattern, filter found devices to match pattern,
    // then return the name back when finished
    fun changeMyDeviceName() {

    }

    fun changeMyDeviceNameBack() {}

    companion object {
        const val SERVICE_UUID = "133f71c6-b7b6-437e-8fd1-d2f59cc76066"
    }
}
