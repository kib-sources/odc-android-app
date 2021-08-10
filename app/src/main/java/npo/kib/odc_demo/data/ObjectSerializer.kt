package npo.kib.odc_demo.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import npo.kib.odc_demo.core.getString
import npo.kib.odc_demo.core.loadPublicKey
import npo.kib.odc_demo.data.models.Blockchain
import java.io.*
import java.security.PublicKey
import java.util.*

class ObjectSerializer {
    @Throws(IOException::class, ClassNotFoundException::class)
    fun deserializeBytes(bytes: ByteArray?): Any {
        val bytesIn = ByteArrayInputStream(bytes)
        val ois = ObjectInputStream(bytesIn)
        val obj: Any = ois.readObject()
        ois.close()
        return obj
    }


    @Throws(IOException::class)
    fun serializeObject(obj: Any?): ByteArray {
        val bytesOut = ByteArrayOutputStream()
        val oos = ObjectOutputStream(bytesOut)
        oos.writeObject(obj)
        oos.flush()
        val bytes: ByteArray = bytesOut.toByteArray()
        bytesOut.close()
        oos.close()
        return bytes
    }

    fun toObject(stringValue: String): Blockchain {
        return Json.decodeFromString(Blockchain.serializer(), stringValue)
    }

    fun toJson(blockchain: Blockchain): String {
        return Json.encodeToString(Blockchain.serializer(), blockchain)
    }
}

object UUIDSerializer : KSerializer<UUID?> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID? {
        val str = decoder.decodeString()
        return if (str == "null") null
        else UUID.fromString(str)
    }

    override fun serialize(encoder: Encoder, value: UUID?) {
        encoder.encodeString(value.toString())
    }
}

object PublicKeySerializer : KSerializer<PublicKey?> {
    override val descriptor = PrimitiveSerialDescriptor("PublicKey", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): PublicKey? {
        val str = decoder.decodeString()
        return if (str.isEmpty()) null
        else
            loadPublicKey(str)
    }

    override fun serialize(encoder: Encoder, value: PublicKey?) {
        if (value != null) {
            encoder.encodeString(value.getString())
        }
    }
}

