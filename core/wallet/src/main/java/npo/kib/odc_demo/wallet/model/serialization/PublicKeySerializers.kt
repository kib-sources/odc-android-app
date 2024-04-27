package npo.kib.odc_demo.wallet.model.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import npo.kib.odc_demo.wallet.Crypto.asPlainString
import npo.kib.odc_demo.wallet.Crypto.generatePublicKey
import java.security.PublicKey

object PublicKeySerializer : KSerializer<PublicKey?> {
    override val descriptor = PrimitiveSerialDescriptor(
        "PublicKey", STRING
    )

    override fun deserialize(decoder: Decoder): PublicKey? {
        val str = decoder.decodeString()
        return if (str.isEmpty()) null
        else str.generatePublicKey()
    }

    override fun serialize(
        encoder: Encoder, value: PublicKey?
    ) {
        if (value != null) {
            encoder.encodeString(value.asPlainString())
        }
    }
}

object PublicKeySerializerNotNull : KSerializer<PublicKey> {
    override val descriptor = PrimitiveSerialDescriptor(
        "PublicKey", STRING
    )

    override fun serialize(encoder: Encoder, value: PublicKey) {
        encoder.encodeString(value.asPlainString())
    }

    override fun deserialize(decoder: Decoder): PublicKey =
        decoder.decodeString().generatePublicKey()
}