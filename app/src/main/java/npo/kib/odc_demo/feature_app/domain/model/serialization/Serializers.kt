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
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.DataPacketType
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.AcceptanceBlocks
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.AmountRequest
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.BanknotesList
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.Block
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.DataPacketVariant
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.TransactionResult
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.UserInfo
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.legacy.PayloadContainer
import java.security.PublicKey
import java.util.UUID

object BytesToTypeConverter {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    /**
     * Specify generic type as type of [DataPacketVariant] to deserialize [ByteArray] to given type
     * */
    inline fun <reified T : DataPacketVariant> ByteArray.deserializeToDataPacketType(): T {
        val json = Json {
            ignoreUnknownKeys = true
        }
        val cbor = CBORObject.DecodeFromBytes(this)
        return json.decodeFromString(cbor.ToJSONString())
    }

    fun ByteArray.deserializeToDataPacket(): DataPacket {
        val cbor = CBORObject.DecodeFromBytes(this)
        return json.decodeFromString(cbor.ToJSONString())
    }

    /**
     * Fully deserializes [DataPacket] based on [DataPacketType]
     * @return [Pair] of [DataPacketType] and [DataPacketVariant]
     * */
    fun ByteArray.deserializeToTypeAndPacketPair(): Pair<DataPacketType, DataPacketVariant> {
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
                DataPacketType.RESULT -> deserializeToDataPacketType<TransactionResult>()
            }
        }
        return packetType to deserializedPacket
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
                DataPacketType.RESULT -> deserializeToDataPacketType<TransactionResult>()
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
     * Objects of [DataPacketVariant] can be serialized to [ByteArray] and sent in [DataPacket]
     * */
    fun DataPacketVariant.serializeToByteArray(): ByteArray {
        val jsonString = json.encodeToString(this)
        return CBORObject.FromJSONString(jsonString).EncodeToBytes()
    }

    fun DataPacket.serializeToByteArray(): ByteArray {
        val jsonString = json.encodeToString(this)
        return CBORObject.FromJSONString(jsonString).EncodeToBytes()
    }
}

object PayloadContainerSerializer {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    //Kotlin Cbor serializer не преобразовывал вложенные классы в PayloadContainer,
    // поэтому используется сторонняя библиотека
    fun ByteArray.toPayloadContainer(): PayloadContainer {
        val cbor = CBORObject.DecodeFromBytes(this)
        return json.decodeFromString(cbor.ToJSONString())
    }

    fun PayloadContainer.toByteArray(): ByteArray {
        val jsonString = json.encodeToString(this)
        return CBORObject.FromJSONString(jsonString).EncodeToBytes()
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