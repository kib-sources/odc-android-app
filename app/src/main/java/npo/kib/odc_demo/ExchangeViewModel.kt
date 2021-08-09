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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import npo.kib.odc_demo.data.BankRepository
import npo.kib.odc_demo.data.P2PConnection

class ExchangeViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = BankRepository(application)
    private val p2p = P2PConnection(application)
    val isConnectedFlow = p2p.isConnected

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

    fun getSum() = repo.getSum()

    fun startAdvertising() {
        p2p.startAdvertising()
    }

    fun startDiscovery() {
        p2p.startDiscovery()
    }

    //TODO A -> B
    fun send(amount: Int) {

    }
}