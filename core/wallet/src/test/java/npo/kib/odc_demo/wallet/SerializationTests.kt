package npo.kib.odc_demo.wallet

import com.upokecenter.cbor.CBORObject
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import npo.kib.odc_demo.common_jvm.ByteArrayToChunksConverter.toByteArrayFromChunks
import npo.kib.odc_demo.common_jvm.ByteArrayToChunksConverter.toChunksList
import npo.kib.odc_demo.wallet.model.Banknote
import npo.kib.odc_demo.wallet.model.BanknoteWithBlockchain
import npo.kib.odc_demo.wallet.model.BanknoteWithProtectedBlock
import npo.kib.odc_demo.wallet.model.ProtectedBlock
import npo.kib.odc_demo.wallet.model.data_packet.DataPacket
import npo.kib.odc_demo.wallet.model.data_packet.DataPacketType.USER_INFO
import npo.kib.odc_demo.wallet.model.data_packet.variants.BanknotesList
import npo.kib.odc_demo.wallet.model.data_packet.variants.DataPacketVariant
import npo.kib.odc_demo.wallet.model.data_packet.variants.UserInfo
import npo.kib.odc_demo.wallet.model.serialization.BytesToTypeConverter.deserializeToDataPacket
import npo.kib.odc_demo.wallet.model.serialization.BytesToTypeConverter.deserializeToDataPacketType
import npo.kib.odc_demo.wallet.model.serialization.BytesToTypeConverter.deserializeToDataPacketVariant
import npo.kib.odc_demo.wallet.model.serialization.TypeToBytesConverter.serializeToByteArray
import npo.kib.odc_demo.wallet.model.serialization.TypeToBytesConverter.toSerializedDataPacket
import npo.kib.odc_demo.wallet.model.serialization.bytesToInt
import npo.kib.odc_demo.wallet.model.serialization.toBytes
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class SerializationTests {

    @Test
    fun testIntToByteArrayAndBack(){
        for(i in (Int.MIN_VALUE..Int.MAX_VALUE)){
            if (i % 10000000 == 0) println("testIntToByteArrayAndBack: $i processed")
            assertEquals(i, i.toBytes().bytesToInt())
        }
    }


    @Test
    fun testReduce() {
        runTest {
            val initial = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 'a'.code.toByte())
            val inList = initial.map { byteArrayOf(it) }
            initial.contentToString().p("Initial barray:\n")
            inList.joinToString {
                it.first().toString()
            }.p("Initial list:\n")
            inList.toByteArrayFromChunks().contentToString().p("Result barray")
        }
    }

    @Test
    fun testBytesToChunksAndBack_x10_000() {
        runTest {
            val chunkSize = 1024
            var cyclesDone = 0
            for (i in (1..10000)) {
//                    launch { //total time higher with launch
                val initial =
                    ByteArray(chunkSize * (0..100).random() + (1 until chunkSize).random()) {
                        (0..Byte.MAX_VALUE).random().toByte()
                    }
                val result = initial.toChunksList(chunkSize).toByteArrayFromChunks()
                assert(initial.size == result.size) { "Sizes are different:\nInitial size = ${initial.size}\nResult size = ${result.size}" }
                result.forEachIndexed { index, byte ->
                    assertEquals(
                        byte, initial[index]
                    ) { "Bytes did not match\nArray size = ${result.size}\nAt index = $index" }
                }
                cyclesDone++
                if (cyclesDone % 1000 == 0) println("Running testBytesToChunksAndBack :\n$cyclesDone cycles done")
            }
//            }
        }
    }

    @Test
    fun testChunkSizesAreValid() {
        runTest {
            var cyclesDone = 0
            for (chunkSize in (1..10_000)) {
                val arr = ByteArray(chunkSize * (0..100).random() + (1..chunkSize).random()) {
                    (0..Byte.MAX_VALUE).random().toByte()
                }
                val chunked = arr.toChunksList(chunkSize)
                chunked.forEach {
                    assert(it.size <= chunkSize) { "Subarray size is > chunkSize.\nGot size: ${it.size}\nExpected size: $chunkSize " }
                }
                cyclesDone++
                if (cyclesDone % 1000 == 0) println("Running testChunkSizesAreValid :\n$cyclesDone cycles done")
            }
        }
    }


    @Test
    fun testChunkedCborSerializeDeserialize() {
        runTest {
            val initial = UserInfo(
                    "TestName",
                    "TestWID"
                )
            assertSameObjectSerializeDeserialize(initial)
            val result = initial.toSerializedDataPacket()
                .toChunksList(10)
                .toByteArrayFromChunks()
                .deserializeToDataPacketVariant()
            assertEquals(initial, result)
        }
    }

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

    private fun Any.p(startMsg: String = "") = println(startMsg + this)

    private fun List<*>.pList(startMsg: String = ""): List<*> {
        println(startMsg + this.toString())
        return this
    }

    @Test
    fun testBanknotesListSerAndDeser() {
        val mList: MutableList<BanknoteWithBlockchain> = mutableListOf()
        for (i in (0..999)) {
            mList.add(
                BanknoteWithBlockchain(
                    banknoteWithProtectedBlock = BanknoteWithProtectedBlock(
                        banknote = Banknote(
                            bin = 5550 * i,
                            amount = 1467 * i,
                            currencyCode = 9211 * i,
                            bnid = "Jesslyn",
                            signature = "Daysha",
                            time = 7525 * i
                        ),
                        protectedBlock = ProtectedBlock(
                            parentSok = null,
                            parentSokSignature = null,
                            parentOtokSignature = null,
                            refUuid = null,
                            sok = null,
                            sokSignature = null,
                            otokSignature = "Cherese",
                            transactionSignature = "Cindi",
                            time = 826 * i
                        ),
                    ), blocks = listOf()
                )
            )
        }
        val bList = BanknotesList(
                list = mList.toList()
            )
        val serializedBList = bList.toSerializedDataPacket()
        println("Serialized list size in bytes :\n" + serializedBList.size)
        assertEquals(bList, serializedBList.deserializeToDataPacketVariant())
    }


    @DisplayName("Test if an object serialized and deserialized back is still the same")
    private fun assertSameObjectSerializeDeserialize(dataPacket: DataPacketVariant) = assertEquals(
        dataPacket, dataPacket.toSerializedDataPacket().deserializeToDataPacketVariant()
    )

    @Test
    @DisplayName("Test [DataPacket] serialization and deserialization")
    fun testUserInfoDataPacketSerializationAndDeserialization() {
        val primitiveByteArray = ByteArray(1270) { it.toByte() }
        val userInfo = UserInfo(
                userName = "User",
                walletId = "some_wallet_id"
            )
        val packet = DataPacket(
            packetType = USER_INFO,
            bytes = userInfo.serializeToByteArray()
        )
        val resultPacket =
            packet.printObj().serializeToByteArray().printObj().deserializeToDataPacket().printObj()
        assertEquals(packet, resultPacket)
        val resultUserInfo = resultPacket.bytes.deserializeToDataPacketType<UserInfo>().printObj()
        assertEquals(userInfo, resultUserInfo)
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    @Serializable
    private data class Pair1<out A, out B>(
        @Serializable val p1: A, @Serializable val p2: B
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
    private inline fun <reified A, reified B> Pair1<A, B>.serialize(): ByteArray {
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
    fun testGenericSerializeAndDeserialize() {

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
            obj.printObj()
                .serializeToByteArray()
                .printObj()
                .deserializeToSomeSuperInterfaceSubType<ExampleSerializable1>()
                .printObj()
        )
    }


    @Test
    fun testSerializeAndDeserialize3() {
        val obj = ExampleSerializable2(ExampleSerializable1(10))

        assertEquals(
            obj,
            obj.printObj()
                .serializeToByteArray()
                .printObj()
                .deserializeToSomeSuperInterfaceSubType<ExampleSerializable2>()
                .printObj()
        )
    }

    private fun <T> T.printObj(): T {
        println(this.toString())
        return this
    }

}