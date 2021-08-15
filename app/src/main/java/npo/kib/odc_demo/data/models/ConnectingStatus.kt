package npo.kib.odc_demo.data.models

import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionResolution

sealed class ConnectingStatus {
    object NoConnection : ConnectingStatus()

    data class ConnectionInitiated(val info: ConnectionInfo) :
        ConnectingStatus()

    data class ConnectionResult(val result: ConnectionResolution) :
        ConnectingStatus()

    object Disconnected : ConnectingStatus()
}
