package npo.kib.odc_demo.core.wallet.model.serialization

import com.upokecenter.cbor.CBORObject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import npo.kib.odc_demo.core.wallet.model.data_packet.DataPacket
import npo.kib.odc_demo.core.wallet.model.data_packet.DataPacketType.*
import npo.kib.odc_demo.core.wallet.model.data_packet.DataPacketType
import npo.kib.odc_demo.core.wallet.model.data_packet.variants.*
import java.nio.ByteBuffer

fun Int.toBytes(): ByteArray =
    ByteBuffer.allocate(Int.SIZE_BYTES).putInt(this).array()

fun ByteArray.bytesToInt(): Int =
    ByteBuffer.wrap(this).int


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
     * Fully deserializes [DataPacket] based on [DataPacketType] that it contains.
     * @return [DataPacketVariant]
     * */
    fun ByteArray.deserializeToDataPacketVariant(): DataPacketVariant {
        val dataPacket = this.deserializeToDataPacket()
        val packetType = dataPacket.packetType
        val packetBytes = dataPacket.bytes
        return with(packetBytes) {
            when (packetType) {
                USER_INFO -> deserializeToDataPacketType<UserInfo>()
                AMOUNT_REQUEST -> deserializeToDataPacketType<AmountRequest>()
                BANKNOTES_LIST -> deserializeToDataPacketType<BanknotesList>()
                ACCEPTANCE_BLOCKS -> deserializeToDataPacketType<AcceptanceBlocks>()
                SIGNED_BLOCK -> deserializeToDataPacketType<Block>()
                TRANSACTION_RESULT -> deserializeToDataPacketType<TransactionResult>()
            }
        }
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
        val jsonString = npo.kib.odc_demo.core.wallet.model.serialization.TypeToBytesConverter.json.encodeToString(this)
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
            packetType, serializeToByteArray()
        )
        val jsonString = npo.kib.odc_demo.core.wallet.model.serialization.TypeToBytesConverter.json.encodeToString(resultDataPacket)
        return CBORObject.FromJSONString(jsonString).EncodeToBytes()
    }
}

