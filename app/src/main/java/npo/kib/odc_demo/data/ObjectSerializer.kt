package npo.kib.odc_demo.data

import java.io.*

class ObjectSerializer {
    @Throws(IOException::class, ClassNotFoundException::class)
    fun deserializeBytes(bytes: ByteArray?): Any? {
        val bytesIn = ByteArrayInputStream(bytes)
        val ois = ObjectInputStream(bytesIn)
        val obj: Any = ois.readObject()
        ois.close()
        return obj
    }


    @Throws(IOException::class)
    fun serializeObject(obj: Any?): ByteArray? {
        val bytesOut = ByteArrayOutputStream()
        val oos = ObjectOutputStream(bytesOut)
        oos.writeObject(obj)
        oos.flush()
        val bytes: ByteArray = bytesOut.toByteArray()
        bytesOut.close()
        oos.close()
        return bytes
    }
}