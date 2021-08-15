package npo.kib.odc_demo

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import npo.kib.odc_demo.data.BankRepository

class ExchangeViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = BankRepository(application)

    val connectionResult = repo.connectionResult
    private val _sum = repo.getSum()
    val sum: StateFlow<Int?> = _sum.stateIn(
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
        repo.startAdvertising()
    }

    fun startDiscovery() {
        repo.startDiscovery()
    }

    fun acceptConnection() {
        repo.acceptConnection()
    }

    fun rejectConnection() {
        repo.rejectConnection()
    }

    fun send(amount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.send(amount)
        }
    }
}