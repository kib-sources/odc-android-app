package npo.kib.odc_demo

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import npo.kib.odc_demo.data.BankRepository
import npo.kib.odc_demo.data.ObjectSerializer
import npo.kib.odc_demo.data.P2PConnection
import npo.kib.odc_demo.data.models.Blockchain

class ExchangeViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = BankRepository(application)
    private val p2p = P2PConnection(application)
    private val serializer = ObjectSerializer()
    val isConnectedFlow = p2p.isConnected
    private val _sum = repo.getSum()
    val sum: StateFlow<Int> = _sum.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun issueBanknotes(amount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val wallet = repo.registerWallet()
            //Получаем список <Banknotes, Block, ProtectedBlock>
            val iB = repo.issueBanknotes(amount, wallet)
            for (b in iB) {
                Log.d("OpenDigitalCash", b.first.toString())
                Log.d("OpenDigitalCash", b.second.toString())
                Log.d("OpenDigitalCash", b.third.toString())
            }

        }
    }

    fun startAdvertising() {
        p2p.startAdvertising()
    }

    fun startDiscovery() {
        p2p.startDiscovery()
    }

    fun send(amount: Int) {
        //  var blockchains = arrayListOf<Blockchain>()
        viewModelScope.launch(Dispatchers.IO) {
            val blockchainArray = repo.getBlockchainsByAmount(amount)
            var blockchainBytes: ByteArray
            for (blockchain in blockchainArray) {
                blockchainBytes = serializer.toJson(blockchain).toByteArray()
                p2p.send(blockchainBytes)
            }

        }
    }
}