package npo.kib.odc_demo.feature_app.domain.model.serialization

import com.upokecenter.cbor.CBORObject
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import npo.kib.odc_demo.feature_app.domain.core.getString
import npo.kib.odc_demo.feature_app.domain.core.loadPublicKey
import npo.kib.odc_demo.feature_app.domain.model.DataPacket
import npo.kib.odc_demo.feature_app.domain.model.serialization.TypeToBytesConverter.serializeToByteArray
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.DataPacketType
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.*
import java.security.PublicKey
import java.util.UUID

object BytesToTypeConverter {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    /**
     *  ___!Should not be used in general code!___
     * Specify generic type as type of [DataPacketVariant] to deserialize [ByteArray] to given type
     * */
    internal inline fun <reified T : DataPacketVariant> ByteArray.deserializeToDataPacketType(): T {
        val json = Json {
            ignoreUnknownKeys = true
        }
        val cbor = CBORObject.DecodeFromBytes(this)
        return json.decodeFromString(cbor.ToJSONString())
    }

    /**
     *  ___!Should not be used in general code!___
     * */
    internal fun ByteArray.deserializeToDataPacket(): DataPacket {
        val cbor = CBORObject.DecodeFromBytes(this)
        return json.decodeFromString(cbor.ToJSONString())
    }

    /**
     * Fully deserializes [DataPacket] based on [DataPacketType]
     * @return [DataPacketVariant]
     * */
    fun ByteArray.deserializeToDataPacketVariant(): DataPacketVariant {
        val dataPacket = this.deserializeToDataPacket()
        val packetType = dataPacket.packetType
        val packetBytes = dataPacket.bytes
        val deserializedPacket: DataPacketVariant
        with(packetBytes) {
            deserializedPacket = when (packetType) {
                DataPacketType.USER_INFO -> deserializeToDataPacketType<UserInfo>()
                DataPacketType.AMOUNT_REQUEST -> deserializeToDataPacketType<AmountRequest>()
                DataPacketType.BANKNOTES_LIST -> deserializeToDataPacketType<BanknotesList>()
                DataPacketType.ACCEPTANCE_BLOCKS -> deserializeToDataPacketType<AcceptanceBlocks>()
                DataPacketType.SIGNED_BLOCK -> deserializeToDataPacketType<Block>()
                DataPacketType.TRANSACTION_RESULT -> deserializeToDataPacketType<TransactionResult>()
            }
        }
        return deserializedPacket
    }


}

object TypeToBytesConverter {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    /**
     *  ___!Should not be used in general code!___
     * Objects of [DataPacketVariant] can be serialized to [ByteArray] and sent in [DataPacket]
     * */
    internal fun DataPacketVariant.serializeToByteArray(): ByteArray {
        val jsonString = json.encodeToString(this)
        return CBORObject.FromJSONString(jsonString).EncodeToBytes()
    }

    /**
     *  ___!Should not be used in general code!___
     * */
    internal fun DataPacket.serializeToByteArray(): ByteArray {
        val jsonString = json.encodeToString(this)
        return CBORObject.FromJSONString(jsonString).EncodeToBytes()
    }

    /**
     *  Creates a new [DataPacket] from this [DataPacketVariant] and serializes it.
     *  @return new [DataPacket] serialized as [ByteArray]
     * */
    fun DataPacketVariant.toSerializedDataPacket(): ByteArray {
        val resultDataPacket = DataPacket(
            packetType,
            serializeToByteArray()
        )
        val jsonString = json.encodeToString(resultDataPacket)
        return CBORObject.FromJSONString(jsonString).EncodeToBytes()
    }
}

object UUIDSerializer : KSerializer<UUID?> {
    override val descriptor = PrimitiveSerialDescriptor(
        "UUID",
        PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): UUID? {
        val str = decoder.decodeString()
        return if (str == "null") null
        else UUID.fromString(str)
    }

    override fun serialize(
        encoder: Encoder,
        value: UUID?
    ) {
        encoder.encodeString(value.toString())
    }
}

object UUIDSerializerNotNull : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor(
        "UUID",
        PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): UUID = UUID.fromString(decoder.decodeString())

    override fun serialize(
        encoder: Encoder,
        value: UUID
    ) {
        encoder.encodeString(value.toString())
    }
}

object PublicKeySerializer : KSerializer<PublicKey?> {
    override val descriptor = PrimitiveSerialDescriptor(
        "PublicKey",
        PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): PublicKey? {
        val str = decoder.decodeString()
        return if (str.isEmpty()) null
        else str.loadPublicKey()
    }

    override fun serialize(
        encoder: Encoder,
        value: PublicKey?
    ) {
        if (value != null) {
            encoder.encodeString(value.getString())
        }
    }
}

object PublicKeySerializerNotNull : KSerializer<PublicKey> {
    override val descriptor = PrimitiveSerialDescriptor(
        "PublicKey",
        PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): PublicKey = decoder.decodeString().loadPublicKey()

    override fun serialize(
        encoder: Encoder,
        value: PublicKey
    ) {
        encoder.encodeString(value.getString())
    }
}