package npo.kib.odc_demo.feature_app.domain.model.serialization

import com.upokecenter.cbor.CBORObject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.CustomType
import kotlin.reflect.KClass

object BytesToTypeConverter {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    /**
     * Pass a child of [CustomType]::class to constructor to deserialize [ByteArray] to given object
     * */
    fun ByteArray.deserializeToObject(type: KClass<out CustomType>): CustomType {
        val cbor = CBORObject.DecodeFromBytes(this)
        return json.decodeFromString(cbor.ToJSONString())
    }

}