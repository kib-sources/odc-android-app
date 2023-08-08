package npo.kib.odc_demo.feature_app.data.models

import com.google.android.gms.nearby.connection.ConnectionInfo

sealed class ConnectingStatus {
    object NoConnection : ConnectingStatus()

    data class ConnectionInitiated(val info: ConnectionInfo) : ConnectingStatus()

    data class ConnectionResult(val statusCode: Int) : ConnectingStatus()

    object Disconnected : ConnectingStatus()
}