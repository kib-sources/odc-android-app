package npo.kib.odc_demo

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.runBlocking
import npo.kib.odc_demo.data.P2pReceiveUseCase
import npo.kib.odc_demo.data.models.ConnectingStatus
import npo.kib.odc_demo.data.p2p.P2pConnectionBidirectionalTcpImpl
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun serverTest(): Unit = runBlocking {
        val connection = P2pConnectionBidirectionalTcpImpl(appContext)
        connection.startAdvertising()

        connection.receivedBytes.collect {
            myLogs("collected ${it.decodeToString()}")
            connection.send("hello from unit test".encodeToByteArray())
        }
    }

    @Test
    fun clientTest(): Unit = runBlocking {
        val connection = P2pConnectionBidirectionalTcpImpl(appContext, "192.168.1.117")
        connection.startDiscovery()

        connection.receivedBytes.collect {
            myLogs("collected ${it.decodeToString()}")
            connection.send("hello from unit test".encodeToByteArray())
        }
    }

    @Test
    fun p2pUseCaseTest(): Unit = runBlocking {
        val receiveUseCase = P2pReceiveUseCase(appContext)
        receiveUseCase.startDiscovery()
        receiveUseCase.p2p.receivedBytes.collect { myLogs(it.decodeToString()) }
    }
}