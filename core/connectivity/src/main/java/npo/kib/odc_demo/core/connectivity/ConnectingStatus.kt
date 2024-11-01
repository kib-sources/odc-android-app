package npo.kib.odc_demo.core.connectivity

import com.google.android.gms.nearby.connection.ConnectionInfo

sealed class ConnectingStatus {
    data object NoConnection : ConnectingStatus()

    data class ConnectionInitiated(val info: ConnectionInfo) : ConnectingStatus()

    data class ConnectionResult(val statusCode: Int) : ConnectingStatus()

    data object Disconnected : ConnectingStatus()
}

