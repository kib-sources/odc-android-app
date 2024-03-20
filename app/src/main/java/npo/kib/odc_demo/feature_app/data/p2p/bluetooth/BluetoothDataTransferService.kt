package npo.kib.odc_demo.feature_app.data.p2p.bluetooth

import android.bluetooth.BluetoothSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import npo.kib.odc_demo.feature_app.domain.util.log
import java.io.IOException

class BluetoothDataTransferService(
    private val socket: BluetoothSocket, private val bufferSize: Int = 300_000 //todo fix transferring big amounts of data
) {
    fun listenForIncomingBytes(): Flow<ByteArray> {
        return flow {
            if (!socket.isConnected) {
                return@flow
            }
            val buffer = ByteArray(bufferSize)
            while (true) {
                val byteCount = try {
                    socket.inputStream.read(buffer)
                } catch (e: IOException) {
                    throw TransferFailedException(e.message ?: "null")
                }
                this@BluetoothDataTransferService.log("Received bytes:\nBuffer size: ${bufferSize}\n" +
                        "Packet byte count: $byteCount")
                emit(
                    buffer.copyOf(byteCount)
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun sendBytes(bytes: ByteArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                this@BluetoothDataTransferService.log("Trying to send bytes...\n" +
                        "Buffer size: ${bufferSize}\n" +
                        "Packet byte count: ${bytes.size}")
                socket.outputStream.write(bytes)
            } catch (e: IOException) {
                e.printStackTrace()
                return@withContext false
            }
            true
        }
    }

    class TransferFailedException(message : String = "") : Exception(message){
        override fun toString(): String {
            return this::class.simpleName + " : " + message
        }
    }
}