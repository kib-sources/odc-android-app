package npo.kib.odc_demo.feature_app.data.p2p.bluetooth

import android.bluetooth.BluetoothSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.BluetoothDataPacket
import java.io.IOException

class BluetoothDataTransferService(
    private val socket: BluetoothSocket
) {
    fun listenForIncomingMessages(): Flow<BluetoothDataPacket> {
//        return flow {
//            if(!socket.isConnected) {
//                return@flow
//            }
//            val buffer = ByteArray(1024)
//            while(true) {
//                val byteCount = try {
//                    socket.inputStream.read(buffer)
//                } catch(e: IOException) {
//                    throw TransferFailedException()
//                }
//
//                emit(
//                    buffer.decodeToString(
//                        endIndex = byteCount
//                    ).toBluetoothMessage(
//                        isFromLocalUser = false
//                    )
//                )
//            }
//        }.flowOn(Dispatchers.IO)
        return flow<BluetoothDataPacket> {}.flowOn(Dispatchers.IO)
    }

    suspend fun sendMessage(bytes: ByteArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                socket.outputStream.write(bytes)
            } catch(e: IOException) {
                e.printStackTrace()
                return@withContext false
            }

            true
        }
    }
}