package npo.kib.odc_demo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ExchangeViewModel::class.java]
        val button = view.findViewById<Button>(R.id.button)
        val buttonAdv = view.findViewById<Button>(R.id.button2)
        val buttonReceive = view.findViewById<Button>(R.id.button10)
        val buttonSend = view.findViewById<Button>(R.id.button4)
        val amountEditText = view.findViewById<EditText>(R.id.editTextNumber2)
        val sumView = view.findViewById<TextView>(R.id.sum)

        button.setOnClickListener {
            val editText = view.findViewById<EditText>(R.id.editTextNumber)
            val amount = editText.text.toString().toInt()
            if (amount > 0) viewModel.issueBanknotes(amount)
        }

        buttonAdv.setOnClickListener {
            viewModel.startAdvertising()
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.connectionResult.collect {
                    //TODO сделать UI для всех состояний, добавить значок загрузки
                    withContext(Dispatchers.Main) {
                        when (it) {
                            is ConnectingStatus.ConnectionInitiated -> showConnectionDialog(it.info)
                            is ConnectingStatus.ConnectionResult ->
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
                            ConnectingStatus.Disconnected -> {
                                buttonSend.isEnabled = false
                                makeToast("Соединение разорвано.")
                            }
                            ConnectingStatus.NoConnection -> buttonSend.isEnabled = false
                        }
                    }

                }
            }
        }

        buttonReceive.setOnClickListener {
            if (askForPermissions()) {
                viewModel.startDiscovery()
            }
        }

        buttonSend.setOnClickListener {
            val amount = amountEditText.text.toString().toInt()
            if (amount > 0) viewModel.send(amountEditText.text.toString().toInt())
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

    private fun showConnectionDialog(info: ConnectionInfo) {
        AlertDialog.Builder(requireContext())
            .setTitle("Принять подключение к пользователю " + info.endpointName)
            .setMessage("Убедитесь, что код совпадает на обоих устройствах: " + info.authenticationDigits)
            .setPositiveButton(
                "Принять"
            ) { _, _ ->  // The user confirmed, so we can accept the connection.
                viewModel.acceptConnection()
            }
            .setNegativeButton(
                getString(R.string.chancel)
            ) { _, _ ->  // The user canceled, so we should reject the connection.
                viewModel.rejectConnection()
            }
            //           .setIcon(ContextCompat.getDrawable(view.context, R.drawable.ic_dialog_alert))
            .show()
    }

    //Runtime permissions

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Необходимо разрешение на определение местоположения")
            .setMessage("Пожалуйста, предоставьте его в Настройках")
            .setPositiveButton(
                "Настройки"
            ) { _, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", requireActivity().application.packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.chancel), null)
            .show()
    }

    private val activityResultLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            var allAreGranted = true
            for (b in result.values) {
                allAreGranted = allAreGranted && b
            }

            if (allAreGranted) {
                viewModel.startDiscovery()
            } else {
                makeToast("Вы не сможете обмениваться банкнотами, пока не предоставите разрешение")
            }
        }

    private fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun askForPermissions(): Boolean {
        val permission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACCESS_FINE_LOCATION
            else Manifest.permission.ACCESS_COARSE_LOCATION
        if (!isPermissionsAllowed()) {
            if (shouldShowRequestPermissionRationale(
                    permission
                )
            ) {
                showPermissionDeniedDialog()
            } else {
                activityResultLauncher.launch(arrayOf(permission))
            }
            return false
        }
        return true
    }
}