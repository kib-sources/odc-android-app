package npo.kib.odc_demo.feature_app.domain.model.serialization

import com.upokecenter.cbor.CBORObject
import kotlinx.serialization.json.Json
import npo.kib.odc_demo.feature_app.domain.model.DataPacket
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.DataPacketTypeMarker

object BytesToTypeConverter {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    /**
     * Pass a child of [DataPacketTypeMarker]::class to constructor to deserialize [ByteArray] to given object
     * */
    inline fun <reified T : DataPacketTypeMarker> ByteArray.deserializeToDataPacketType(): T {
        val json = Json {
            ignoreUnknownKeys = true
        }
        val cbor = CBORObject.DecodeFromBytes(this)
        return json.decodeFromString(cbor.ToJSONString())
    }

    fun ByteArray.deserializeToDataPacket() : DataPacket {
        val cbor = CBORObject.DecodeFromBytes(this)
        return json.decodeFromString(cbor.ToJSONString())
    }



}