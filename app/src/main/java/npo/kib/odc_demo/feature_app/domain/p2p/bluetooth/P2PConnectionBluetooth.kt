package npo.kib.odc_demo.feature_app.domain.p2p.bluetooth

import androidx.activity.result.ActivityResultRegistry
import kotlinx.coroutines.flow.Flow
import npo.kib.odc_demo.feature_app.domain.model.connection_status.BluetoothConnectionStatus
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnection

//Have to use because have to pass ActivityResultRegistry which is probably unique to bluetooth
interface P2PConnectionBluetooth : P2PConnection {
    @Deprecated(
        "",
        ReplaceWith("fun startAdvertising(registry: ActivityResultRegistry, duration: Int, callback: (Int?) -> Unit)"),
        level = DeprecationLevel.HIDDEN
    )
    override fun startAdvertising()

    @Deprecated(
        "",
        ReplaceWith("fun stopAdvertising(registry: ActivityResultRegistry)"),
        level = DeprecationLevel.HIDDEN
    )
    override fun stopAdvertising()

    fun startAdvertising(registry: ActivityResultRegistry, duration: Int, callback: (Int?) -> Unit)
    fun stopAdvertising(registry: ActivityResultRegistry)

    fun startBluetoothServerAndGetFlow(): Flow<BluetoothConnectionStatus>

    fun connectToDevice(device: CustomBluetoothDevice): Flow<BluetoothConnectionStatus>

    fun closeConnection()

    fun reset()

}