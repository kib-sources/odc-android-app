package npo.kib.odc_demo

import com.upokecenter.cbor.CBORObject
import io.mockk.InternalPlatformDsl.toStr
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import npo.kib.odc_demo.feature_app.domain.model.DataPacket
import npo.kib.odc_demo.feature_app.domain.model.serialization.BytesToTypeConverter.deserializeToDataPacket
import npo.kib.odc_demo.feature_app.domain.model.serialization.BytesToTypeConverter.deserializeToDataPacketType
import npo.kib.odc_demo.feature_app.domain.model.serialization.TypeToBytesConverter.serializeToByteArray
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.DataPacketType
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.DataPacketVariant
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.UserInfo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class SerializationTests {


    @Test
    @DisplayName("Test all existing obj")
    fun testAllObjectsSerializationAndDeserialization() {
        //Can't initialize a PublicKey easily so will leave serializing crypto-related stuff for now
//        val uuid1 = UUID.fromString("d2f5b12a-84af-11ee-b962-0242ac120002")
//        val uuid2 = UUID.fromString("d2f5b12a-84af-11ee-b962-0242ac120003")
//        val publicKey =
//            "-----BEGIN PUBLIC KEY-----MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCNC7BgdnBIschvRDGNzYHoPGj2uF7GwlmlYRTukLkymv/NtZOTimobbzeswWYR6xNp1jOnlpAGWCxP94pCcfcWlSOp7eXb5nOVkzK32AIVy2lXuzwWlQg4BROItXySMYSo5sZog7e6+OL5XklDep06EppjtX+I9unaUMRye+PkJwIDAQAB-----END PUBLIC KEY-----"
//                .loadPublicKey()
//        val block = Block(
//            uuid1, uuid2, "bnid", publicKey, 100, "asdasd", "asdsad", "asdasd"
//        )
//        testObjectSerializationAndDeserialization<Block>(block)

    }


    @DisplayName("Test if an object serialized and deserialized back is still the same")
    private inline fun <reified T : DataPacketVariant> testObjectSerializationAndDeserialization(
        obj: DataPacketVariant
    ) {
        assertEquals(obj, obj.serializeToByteArray().deserializeToDataPacketType<T>())
    }

    @Test
    @DisplayName("Test [DataPacket] serialization and deserialization")
    fun testDataPacketSerializationAndDeserialization() {
        val primitiveByteArray = ByteArray(1270) { it.toByte() }
        val userInfo = UserInfo(userName = "User", walletId = "some_wallet_id")
        val packet = DataPacket(
            packetType = DataPacketType.USER_INFO, bytes = userInfo.serializeToByteArray()
        )
        val resultPacket =
            packet.printObj().serializeToByteArray().printObj().deserializeToDataPacket().printObj()
        assertEquals(packet, resultPacket)
        val resultUserInfo = resultPacket.bytes.deserializeToDataPacketType<UserInfo>().printObj()
        assertEquals(userInfo, resultUserInfo)
    }

    @Serializable
    private data class Pair1<out A , out B>(
        @Serializable
        val p1: A,
        @Serializable
        val p2: B
    )

    @Test
    fun testPair1() {
        //Cannot serialize and deserialize nested ByteArray and Array<Byte> inside a Pair1, need a List<Byte>
        //Probably a mistake somewhere as it works with DataPacket serialization and deserialization
        //Example error with ByteArray:
        //Expected :Pair1(p1=10, p2=[B@1cdc4c27)
        //Actual   :Pair1(p1=10, p2=[B@27953a83)
        val primitiveByteArray = ByteArray(127) { it.toByte() } //Will not work
        val byteArray = Array(127) { it.toByte() } // Will not work
        val byteList = byteArray.toList() //Will work
        println(primitiveByteArray)
        println(byteArray)
        println(byteList)
        val obj = Pair1(10, byteList)

        val obj2 =
            obj.printObj().serialize().printObj().deserialize<Pair1<Int, List<Byte>>>().printObj()
        assertEquals(obj, obj2)

    }

    //Does not work without reified A & B because of Type Erasure
    private inline fun <reified A , reified B> Pair1<A, B>.serialize(): ByteArray {
        val json = Json {
            ignoreUnknownKeys = true
        }
        val jsonString = json.encodeToString(this)
        return CBORObject.FromJSONString(jsonString).EncodeToBytes()
    }

    private inline fun <reified T : Pair1<Any, Any>> ByteArray.deserialize(): T {
        val json = Json {
            ignoreUnknownKeys = true
        }
        val cbor = CBORObject.DecodeFromBytes(this)
        return json.decodeFromString(cbor.ToJSONString())
    }


    @Test
    fun testSerializeAndDeserialize() {

        @Serializable
        abstract class SomeSuperType

        @Serializable
        data class ExampleSerializable(val a: Int) : SomeSuperType()

        val json = Json {
            ignoreUnknownKeys = true
        }

        fun ExampleSerializable.serialize(): ByteArray {
            val jsonString = json.encodeToString(this)
            return CBORObject.FromJSONString(jsonString).EncodeToBytes()
        }

        fun ByteArray.deserialize(): ExampleSerializable {
            val cbor = CBORObject.DecodeFromBytes(this)
            return json.decodeFromString(cbor.ToJSONString())
        }

        val obj = ExampleSerializable(10)

        assertEquals(obj, obj.serialize().deserialize())
    }


    @Serializable
    private sealed interface SomeSuperInterface

    @Serializable
    private data class ExampleSerializable1(val a: Int) : SomeSuperInterface

    @Serializable
    private data class ExampleSerializable2(val a: ExampleSerializable1) : SomeSuperInterface

    private inline fun <reified T : SomeSuperInterface> ByteArray.deserializeToSomeSuperInterfaceSubType(): T {
        val json = Json {
            ignoreUnknownKeys = true
        }
        val cbor = CBORObject.DecodeFromBytes(this)
        return json.decodeFromString(cbor.ToJSONString())
    }

    private fun SomeSuperInterface.serializeToByteArray(): ByteArray {
        val json = Json {
            ignoreUnknownKeys = true
        }
        val jsonString = json.encodeToString(this)
        return CBORObject.FromJSONString(jsonString).EncodeToBytes()
    }

    @Test
    fun testSerializeAndDeserialize2() {
        val obj = ExampleSerializable1(20)

        assertEquals(
            obj,
            obj.printObj().serializeToByteArray().printObj()
                .deserializeToSomeSuperInterfaceSubType<ExampleSerializable1>().printObj()
        )
    }


    @Test
    fun testSerializeAndDeserialize3() {
        val obj = ExampleSerializable2(ExampleSerializable1(10))

        assertEquals(
            obj,
            obj.printObj().serializeToByteArray().printObj()
                .deserializeToSomeSuperInterfaceSubType<ExampleSerializable2>().printObj()
        )
    }

    private fun <T> T.printObj(): T {
        println(this.toStr())
        return this
    }

}