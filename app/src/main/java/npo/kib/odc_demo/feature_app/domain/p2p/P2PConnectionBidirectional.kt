package npo.kib.odc_demo.feature_app.domain.p2p

import androidx.activity.result.ActivityResultRegistry

interface P2PConnectionBidirectional : P2PConnection {
    fun startAdvertising()
    fun stopAdvertising()
}

//Have to use because have to pass ActivityResultRegistry which is unique to bluetooth
interface P2PConnectionBidirectionalBluetooth : P2PConnectionBidirectional {

    @Deprecated(
        "use startAdvertising(registry: ActivityResultRegistry, duration: Int)",
        ReplaceWith("startAdvertising(registry: ActivityResultRegistry, duration: Int)")
    )
    override fun startAdvertising()
    @Deprecated(
        "use stopAdvertising(registry: ActivityResultRegistry)",
        ReplaceWith("stopAdvertising(registry: ActivityResultRegistry)")
    )
    override fun stopAdvertising()
    fun startAdvertising(registry: ActivityResultRegistry, duration: Int)
    fun stopAdvertising(registry: ActivityResultRegistry)
}