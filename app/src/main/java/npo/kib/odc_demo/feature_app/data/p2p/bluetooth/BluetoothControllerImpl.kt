package npo.kib.odc_demo.feature_app.data.p2p.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.domain.model.connection_status.ConnectingStatus
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.BluetoothController
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.BluetoothDataPacket
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.ConnectionResult
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.CustomBluetoothDevice
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.toByteArray
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

    override val isConnected: StateFlow<Boolean>
        get() = _isConnected.asStateFlow()

    private val _scannedDevices = MutableStateFlow<List<CustomBluetoothDevice>>(emptyList())
    override val scannedDevices: StateFlow<List<CustomBluetoothDevice>>
        get() = _scannedDevices.asStateFlow()

    private val _bondedDevices = MutableStateFlow<List<CustomBluetoothDevice>>(emptyList())
    override val bondedDevices: StateFlow<List<CustomBluetoothDevice>>
        get() = _bondedDevices.asStateFlow()

    private val _errors = MutableSharedFlow<String>()
    override val errors: SharedFlow<String>
        get() = _errors.asSharedFlow()

    private val deviceFoundReceiver = DeviceFoundReceiver { device ->
        _scannedDevices.update { devices ->
            val newDevice = device.toCustomBluetoothDevice()
            if (newDevice in devices) devices else devices + newDevice
        }
    }

    private val bluetoothStateReceiver = BluetoothStateReceiver { isConnected, bluetoothDevice ->
        if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == true) {
            _isConnected.update { isConnected }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit("Can't connect to a non-paired device.")
            }
        }
    }

    private var currentServerSocket: BluetoothServerSocket? = null
    private var currentClientSocket: BluetoothSocket? = null



    override fun startAdvertising(registry: ActivityResultRegistry, duration: Int) {
        val enableDiscoverableLauncher = registry.register(
            "DISCOVERABLE_LAUNCHER", ActivityResultContracts.StartActivityForResult()
        ) {}
        val discoverableIntent: Intent =
            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration)
            }
        Toast.makeText(context, "startAdvertising() invoked from BluetoothController", Toast.LENGTH_SHORT).show()
        enableDiscoverableLauncher.launch(discoverableIntent, ActivityOptionsCompat.makeBasic())

        enableDiscoverableLauncher.unregister()
    }

    //Start advertising for 1 second, no other way to cancel advertising (or disable bluetooth).
    //When bluetooth is enabled back the phone starts advertising by default for some duration.
    override fun stopAdvertising(registry: ActivityResultRegistry) {
        val enableDiscoverableLauncher = registry.register(
            "DISCOVERABLE_LAUNCHER", ActivityResultContracts.StartActivityForResult()
        ) {}
        val discoverableIntent: Intent =
            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1)
            }
        Toast.makeText(context, "stopAdvertising() invoked from BluetoothController", Toast.LENGTH_SHORT).show()
        enableDiscoverableLauncher.launch(discoverableIntent, ActivityOptionsCompat.makeBasic())
        enableDiscoverableLauncher.unregister()
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

    override fun startBluetoothServer(): Flow<ConnectionResult> {
        return flow {
            currentServerSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                "chat_service",
                UUID.fromString(SERVICE_UUID)
            )

            var shouldLoop = true
            while (shouldLoop) {
                currentClientSocket = try {
                    currentServerSocket?.accept()
                } catch (e: IOException) {
                    shouldLoop = false
                    null
                }
                emit(ConnectionResult.ConnectionEstablished)
                currentClientSocket?.let {
                    currentServerSocket?.close()
                    val service = BluetoothDataTransferService(it)
                    dataTransferService = service

                    emitAll(
                        service
                            .listenForIncomingMessages()
                            .map {
                                ConnectionResult.TransferSucceeded(it)
                            }
                    )
                }
            }
        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    //todo get rid of redundant and implement uniform ConnectionResult and etc classes
    override fun connectToDevice(device: CustomBluetoothDevice): Flow<ConnectingStatus.ConnectionResult> {
//        return flow {
//            currentClientSocket = bluetoothAdapter
//                ?.getRemoteDevice(device.address)
//                ?.createRfcommSocketToServiceRecord(
//                    UUID.fromString(SERVICE_UUID)
//                )
//            stopDiscovery()
//
//            currentClientSocket?.let { socket ->
//                try {
//                    socket.connect()
//                    emit(ConnectionResult.ConnectionEstablished)
//
//                    BluetoothDataTransferService(socket).also {
//                        dataTransferService = it
//                        emitAll(
//                            it.listenForIncomingMessages()
//                                .map { packet ->
//                                    ConnectionResult.TransferSucceeded(packet) }
//                        )
//                    }
//                } catch (e: IOException) {
//                    socket.close()
//                    currentClientSocket = null
//                    emit(ConnectionResult.Error("Connection was interrupted"))
//                }
//            }
//        }.onCompletion {
//            closeConnection()
//        }.flowOn(Dispatchers.IO)
        return flow<ConnectingStatus.ConnectionResult> { ConnectingStatus.ConnectionResult(1) }.flowOn(Dispatchers.IO)
    }

    override suspend fun trySendData(data: ByteArray): BluetoothDataPacket? {


        if (dataTransferService == null) {
            return null
        }

        val bluetoothPacket = BluetoothDataPacket(
            bytes = data,
            fromUserName = bluetoothAdapter?.name ?: "Unknown name",
        )

        dataTransferService?.sendMessage(bluetoothPacket.toByteArray())

        return bluetoothPacket
    }

    override fun closeConnection() {
        currentClientSocket?.close()
        currentServerSocket?.close()
        currentClientSocket = null
        currentServerSocket = null
    }

    override fun release() {
        context.unregisterReceiver(deviceFoundReceiver)
        context.unregisterReceiver(bluetoothStateReceiver)
        closeConnection()
    }

    private fun updateBondedDevices() {
        bluetoothAdapter
            ?.bondedDevices
            ?.map { it.toCustomBluetoothDevice() }
            ?.also { devices ->
                _bondedDevices.update { devices }
            }
    }

    companion object {
        const val SERVICE_UUID = "133f71c6-b7b6-437e-8fd1-d2f59cc76066"
    }
}
