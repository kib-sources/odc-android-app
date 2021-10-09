package npo.kib.odc_demo

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import npo.kib.odc_demo.data.p2p.P2pConnectionBidirectionalTcpImpl
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun serverTest(): Unit = runBlocking{
        val connection = P2pConnectionBidirectionalTcpImpl()
        connection.startAdvertising()

        connection.receivedBytes.collect {
            myLogs("collected ${it.decodeToString()}")
            connection.send("hello from unit test".encodeToByteArray())
        }
    }

    @Test
    fun clientTest(): Unit = runBlocking{
        val connection = P2pConnectionBidirectionalTcpImpl("192.168.1.117")
        connection.startDiscovery()

        connection.receivedBytes.collect {
            myLogs("collected ${it.decodeToString()}")
            connection.send("hello from unit test".encodeToByteArray())
        }
    }
}