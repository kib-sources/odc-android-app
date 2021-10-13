package npo.kib.odc_demo

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import npo.kib.odc_demo.data.P2pReceiveUseCase
import npo.kib.odc_demo.data.p2p.P2pConnectionTcpImpl
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class P2pInstrumentedTest {

    private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun serverTest(): Unit = runBlocking {
        val connection = P2pConnectionTcpImpl(appContext)
        connection.startAdvertising()

        connection.receivedBytes.collect {
            myLogs("collected ${it.decodeToString()}")
            connection.send("hello from unit test".encodeToByteArray())
        }
    }

    @Test
    fun clientTest(): Unit = runBlocking {
        val connection = P2pConnectionTcpImpl(appContext, "192.168.1.117")
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

        val res = receiveUseCase.connectionResult.take(2).last()
        myLogs(res)

        receiveUseCase.p2p.receivedBytes.collect { myLogs(it.decodeToString()) }
//        receiveUseCase.requiringStatusFlow.collect { myLogs("status $it") }
    }
}