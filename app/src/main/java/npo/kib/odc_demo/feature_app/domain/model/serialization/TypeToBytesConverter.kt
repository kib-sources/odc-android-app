package npo.kib.odc_demo.feature_app.domain.model.serialization

import com.upokecenter.cbor.CBORObject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import npo.kib.odc_demo.feature_app.domain.model.DataPacket
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.CustomType

object TypeToBytesConverter {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    /**
     * Objects of [CustomType] can be serialized to [ByteArray] and sent in [DataPacket]
     * */
    fun CustomType.toByteArray(): ByteArray {
        val jsonString = json.encodeToString(this)
        return CBORObject.FromJSONString(jsonString).EncodeToBytes()
    }

}