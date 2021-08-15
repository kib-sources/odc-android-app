package npo.kib.odc_demo

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import npo.kib.odc_demo.core.loadPublicKey
import npo.kib.odc_demo.data.models.ConnectingStatus

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

        viewModel = ViewModelProvider(this)[ExchangeViewModel::class.java]
        val button = view.findViewById<Button>(R.id.button)
        val buttonAdv = view.findViewById<Button>(R.id.button2)
        val buttonReceive = view.findViewById<Button>(R.id.button10)
        val buttonSend = view.findViewById<Button>(R.id.button4)
        val amountEditText = view.findViewById<EditText>(R.id.editTextNumber2)
        val sumView = view.findViewById<TextView>(R.id.sum)

        val dialog = AlertDialog.Builder(view.context)
        fun showConnectionDialog(info: ConnectionInfo) {
            dialog.setTitle("Принять подключение к пользователю " + info.endpointName)
                .setMessage("Убедитесь, что код совпадает на обоих устройствах: " + info.authenticationDigits)
                .setPositiveButton(
                    "Принять"
                ) { dialog: DialogInterface?, which: Int ->  // The user confirmed, so we can accept the connection.
                    viewModel.acceptConnection()
                }
                .setNegativeButton(
                    getString(R.string.chancel)
                ) { dialog: DialogInterface?, which: Int ->  // The user canceled, so we should reject the connection.
                    viewModel.rejectConnection()
                }
                //           .setIcon(ContextCompat.getDrawable(view.context, R.drawable.ic_dialog_alert))

                .show()
                .setCanceledOnTouchOutside(true)
        }

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

//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.isConnectedFlow.collect {
//                    buttonSend.isEnabled = it
//                }
//            }
//        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.connectionResult.collect {
                    //TODO сделать UI для всех состояний, добавить значок загрузки
                    when (it) {
                        is ConnectingStatus.ConnectionInitiated ->
                            withContext(Dispatchers.Main) {
                                showConnectionDialog(it.info)
                            }
                        is ConnectingStatus.ConnectionResult ->
                            withContext(Dispatchers.Main) {
                                when (it.result.status.statusCode) {
                                    ConnectionsStatusCodes.STATUS_OK -> {
                                        buttonSend.isEnabled = true
                                        makeToast("Соединение установлено.")
                                    }
                                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                                        makeToast("Соединение отклонено.")
                                    }
                                    ConnectionsStatusCodes.STATUS_ERROR -> {
                                        makeToast("Соединение разорвано.")
                                    }
                                    else -> {
                                        makeToast("Неизвестная ошибка.")
                                    }
                                }
                            }
                        ConnectingStatus.Disconnected ->
                            withContext(Dispatchers.Main) {
                                buttonSend.isEnabled = false
                                makeToast("Соединение разорвано.")
                            }
                    }
                }
            }
        }

        //TODO запросить разрешение на геолокацию
        buttonReceive.setOnClickListener {
            viewModel.startDiscovery()
        }

        buttonSend.setOnClickListener {
            viewModel.send(amountEditText.text.toString().toInt())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sum.collect { sumView.text = it?.toString() ?: "0" }
            }
        }
    }

    private fun Fragment.makeToast(text: String, duration: Int = Toast.LENGTH_LONG) {
        activity?.let {
            Toast.makeText(it, text, duration).show()
        }
    }
}