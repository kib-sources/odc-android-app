package npo.kib.odc_demo.data.p2p

import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import npo.kib.odc_demo.core.getString
import npo.kib.odc_demo.core.loadPublicKey
import npo.kib.odc_demo.data.models.PayloadContainer
import java.security.PublicKey
import java.util.*

class ObjectSerializer {

    val json = Json {
        ignoreUnknownKeys = true
    }

    fun toObject(stringValue: String): PayloadContainer {
        return json.decodeFromString(stringValue,)
    }

    fun toJson(payloadContainer: PayloadContainer): String {
        return json.encodeToString(payloadContainer)
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

object UUIDSerializerNotNull : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID = UUID.fromString(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}

object PublicKeySerializer : KSerializer<PublicKey?> {
    override val descriptor = PrimitiveSerialDescriptor("PublicKey", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): PublicKey? {
        val str = decoder.decodeString()
        return if (str.isEmpty()) null
        else str.loadPublicKey()
    }

    override fun serialize(encoder: Encoder, value: PublicKey?) {
        if (value != null) {
            encoder.encodeString(value.getString())
        }
    }
}

object PublicKeySerializerNotNull : KSerializer<PublicKey> {
    override val descriptor = PrimitiveSerialDescriptor("PublicKey", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): PublicKey = decoder.decodeString().loadPublicKey()

    override fun serialize(encoder: Encoder, value: PublicKey) {
        encoder.encodeString(value.getString())
    }
}