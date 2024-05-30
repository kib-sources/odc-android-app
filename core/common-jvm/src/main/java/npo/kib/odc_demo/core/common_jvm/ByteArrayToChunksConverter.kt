package npo.kib.odc_demo.core.common_jvm

import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

object ByteArrayToChunksConverter {

    const val DEFAULT_PACKET_CHUNK_SIZE: Int = 1024

    //chunk size is amount in bytes that a connection allows to send in one operation
    suspend fun ByteArray.toChunksList(
        chunkSize: Int = DEFAULT_PACKET_CHUNK_SIZE
    ): List<ByteArray> {
        val resultList: MutableList<ByteArray> = mutableListOf()

        for (i in indices step chunkSize) {
            coroutineContext.ensureActive()
            resultList.add(this.copyOfRange(i, minOf(i + chunkSize, this.size)))
        }

        return resultList.toList() //contains the packet bytes in chunks
    }

    suspend fun List<ByteArray>.toByteArrayFromChunks(): ByteArray = reduce { acc, bytes ->
        coroutineContext.ensureActive()
        acc + bytes
    }

}