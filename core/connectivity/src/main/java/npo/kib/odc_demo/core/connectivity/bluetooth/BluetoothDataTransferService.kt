package npo.kib.odc_demo.core.connectivity.bluetooth

import android.bluetooth.BluetoothSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import npo.kib.odc_demo.core.common.data.util.log
import npo.kib.odc_demo.core.wallet.model.serialization.bytesToInt
import npo.kib.odc_demo.core.wallet.model.serialization.toBytes
import java.io.IOException

class BluetoothDataTransferService(
    private val socket: BluetoothSocket
) {
    private val bufferSize: Int = 990 //chunk size == bufferSize

    /** Waits for the next packet's total size in bytes, concatenates all received chunks until
     *  the total size is reached*/
    suspend fun listenForIncomingBytes(): Flow<ByteArray> {
        return flow {
            if (!socket.isConnected) {
                return@flow
            }
            while (true) {
                currentCoroutineContext().ensureActive()
                //First packet received is NextPacketSizeInfo
                val sizeBuffer = ByteArray(4)
                val byteCount = try {
                    socket.inputStream.read(sizeBuffer)
                } catch (e: IOException) {
                    throw TransferFailedException(e.message ?: "null")
                }

                val packet = sizeBuffer.copyOf(byteCount)
                val nextPacketSize = packet.bytesToInt()
                if (byteCount != 4) {
                    this@BluetoothDataTransferService.log("Received wrong packet size info. ByteCount != 4 B  !!!")
                    throw TransferFailedException("Received wrong packet size info. ByteCount != 4 B  !!!")
                }
                this@BluetoothDataTransferService.log(
                    "Received next packet size.\nInfo size: $byteCount B\nContents: ${packet.contentToString()}\n" + "Next packet size as Int: $nextPacketSize"
                )

                var resultBytes = ByteArray(0)
                var chunksReceived = 0
                while (resultBytes.size < nextPacketSize) {
                    currentCoroutineContext().ensureActive()
                    val chunkBuffer = ByteArray(bufferSize)
                    val bCount = try {
                        socket.inputStream.read(chunkBuffer)
                    } catch (e: IOException) {
                        throw TransferFailedException(e.message ?: "null")
                    }
                    chunksReceived++
                    resultBytes += chunkBuffer.copyOf(bCount)
                    this@BluetoothDataTransferService.log("Received a packet chunk #$chunksReceived, size: $bCount B. (Total: ${resultBytes.size} B of $nextPacketSize B )")
                    if (resultBytes.size > nextPacketSize) {
                        this@BluetoothDataTransferService.log("Received more bytes that expected.\n" + "Expected: $nextPacketSize B\nReceived: ${resultBytes.size} B\nCHUNKS RECEIVED: $chunksReceived")
                        throw TransferFailedException("Received more bytes that expected.\n" + "Expected: $nextPacketSize B\nReceived: ${resultBytes.size} B\nCHUNKS RECEIVED: $chunksReceived")
                    }

                }
                this@BluetoothDataTransferService.log("Received all the chunks from the packet! Number of chunks: $chunksReceived")
                emit(resultBytes)
            }
        }.flowOn(Dispatchers.IO)
    }

    /**Sends next packet size in bytes first, then bytes in chunks.*/
    suspend fun sendBytes(bytes: ByteArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val sizeInfoBytes = bytes.size.toBytes()
                this@BluetoothDataTransferService.log("Sending next packet size info as ByteArray. Size = ${sizeInfoBytes.size} B")
                socket.outputStream.write(sizeInfoBytes)
                var chunksSent = 0
                for (index in bytes.indices step bufferSize) {
                    ensureActive()
                    val endIndex = minOf(index + bufferSize, bytes.size)
                    val chunk = bytes.copyOfRange(index, endIndex)// the end of range is exclusive
                    this@BluetoothDataTransferService.log(
                        "Sending next packet chunk #${chunksSent + 1}. Initial bytes last index: ${bytes.lastIndex} . " +
                                "Chunk start index: $index . Chunk end index: ${endIndex - 1} . Chunk size = ${chunk.size} B"
                    )
                    socket.outputStream.write(chunk)
                    chunksSent++
                }
                this@BluetoothDataTransferService.log("Sent all the chunks from the packet! Number of chunks: $chunksSent")
            } catch (e: IOException) {
                e.printStackTrace()
                return@withContext false
            }
            true
        }
    }

    class TransferFailedException(message: String = "") : Exception(message) {
        override fun toString(): String {
            return this::class.simpleName + " : " + message
        }
    }
}