package npo.kib.odc_demo

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PReceiveUseCase
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnection
import npo.kib.odc_demo.feature_app.data.p2p.tcp.P2PConnectionTcpImpl
import npo.kib.odc_demo.common.util.myLogs
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class P2pInstrumentedTest {

    private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun serverTest(): Unit = runBlocking {
        val connection = P2PConnectionTcpImpl(appContext)
        connection.startAdvertising()

        connection.receivedBytes.collect {
            myLogs("collected ${it.decodeToString()}")
            connection.send("hello from unit test".encodeToByteArray())
        }
    }

    @Test
    fun clientTest(): Unit = runBlocking {
        val connection = P2PConnectionTcpImpl(appContext, "192.168.1.117")
        connection.startDiscovery()

        connection.receivedBytes.collect {
            myLogs("collected ${it.decodeToString()}")
            connection.send("hello from unit test".encodeToByteArray())
        }
    }

    @Test
    fun p2pUseCaseTest(): Unit = runBlocking {
        val p2p: P2PConnection = P2PConnectionTcpImpl(appContext, "192.168.1.117")
        val receiveUseCase = P2PReceiveUseCase(appContext, p2p)
        receiveUseCase.startDiscovery()

        val res = receiveUseCase.connectionResult.take(2).last()
        myLogs(res)

//        receiveUseCase.p2p.receivedBytes.collect { myLogs(it.decodeToString()) }
        receiveUseCase.requiringStatusFlow.collect { myLogs("status $it") }
    }
}