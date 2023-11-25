package npo.kib.odc_demo.feature_app.domain.p2p

import kotlinx.coroutines.flow.Flow

interface P2PConnection {

    val receivedBytes: Flow<ByteArray>

    fun startDiscovery()
    fun stopDiscovery()
    
    fun startAdvertising()
    fun stopAdvertising()

    fun acceptConnection()
    fun rejectConnection()

    //returns null if dataTransferService is not online
    //todo change to sendDataPacket
    suspend fun sendBytes(bytes: ByteArray): ByteArray? {
        TODO()
    }

    //todo change to return Flow<DataPacket> instead
    suspend fun listenForBytes() : Flow<ByteArray> {
        TODO()
    }
}