package npo.kib.odc_demo

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ExchangeFragment : Fragment() {

    companion object {
        fun newInstance() = ExchangeFragment()
    }

    private lateinit var viewModel: ExchangeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.exchange_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ExchangeViewModel::class.java)
        val button = view.findViewById<Button>(R.id.button)
        val buttonAdv = view.findViewById<Button>(R.id.button2)
        val buttonReceive = view.findViewById<Button>(R.id.button10)
        val buttonSend = view.findViewById<Button>(R.id.button4)
        val amountEditText = view.findViewById<EditText>(R.id.editTextNumber2)
        val sumView = view.findViewById<TextView>(R.id.sum)

        button.setOnClickListener {
            val editText = view.findViewById<EditText>(R.id.editTextNumber)
            viewModel.issueBanknotes(editText.text.toString().toInt())
        }

        buttonAdv.setOnClickListener {
            viewModel.startAdvertising()
//            p2p.startAdvertising()
//            val bytesPayload = Payload.fromBytes(byteArrayOf(0xa, 0xb, 0xc, 0xd))
//            Log.d("OpenDigitalCashP", p2p.isConnected().toString())
//            if (p2p.isConnected()) {
//                buttonSend.isEnabled
//            }
        }

        viewModel.viewModelScope.launch {
            viewModel.isConnectedFlow.collect {
                buttonSend.isEnabled = it
            }
        }

        //TODO запросить разрешение на геолокацию
        buttonReceive.setOnClickListener {
            viewModel.startDiscovery()
        }

        buttonSend.setOnClickListener {
            viewModel.send(amountEditText.text.toString().toInt())
        }

        viewModel.viewModelScope.launch {
            viewModel.getSum().collect { sumView.text = it.toString() }
        }
    }
}