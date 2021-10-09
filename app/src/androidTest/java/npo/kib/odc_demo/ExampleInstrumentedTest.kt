package npo.kib.odc_demo

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import npo.kib.odc_demo.data.p2p.P2pConnectionBidirectionalTcpImpl
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun serverTest(): Unit = runBlocking{
        val connection = P2pConnectionBidirectionalTcpImpl(appContext)
        connection.startAdvertising()

        connection.receivedBytes.collect {
            myLogs("collected ${it.decodeToString()}")
            connection.send("hello from unit test".encodeToByteArray())
        }
    }

    @Test
    fun clientTest(): Unit = runBlocking{
        val connection = P2pConnectionBidirectionalTcpImpl(appContext, "192.168.1.117")
        connection.startDiscovery()

        connection.receivedBytes.collect {
            myLogs("collected ${it.decodeToString()}")
            connection.send("hello from unit test".encodeToByteArray())
        }
    }

    @Test
    fun p2pUseCaseTest(): Unit = runBlocking{
        val connection = P2pConnectionBidirectionalTcpImpl(appContext, "192.168.1.117")

        connection.receivedBytes.collect {
            myLogs("collected ${it.decodeToString()}")
            connection.send("hello from unit test".encodeToByteArray())
        }
    }
}