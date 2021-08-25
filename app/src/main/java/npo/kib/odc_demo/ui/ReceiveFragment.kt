package npo.kib.odc_demo.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import npo.kib.odc_demo.R
import npo.kib.odc_demo.SwitcherInterface
import npo.kib.odc_demo.makeToast
import npo.kib.odc_demo.data.models.ConnectingStatus
import npo.kib.odc_demo.data.models.RequiringStatus
import npo.kib.odc_demo.data.models.SearchingStatus
import npo.kib.odc_demo.databinding.ReceiveFragmentBinding

class ReceiveFragment : Fragment() {

    companion object {
        fun newInstance() = ReceiveFragment()
    }

    private var _binding: ReceiveFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: ReceiveViewModel
    private lateinit var mController: SwitcherInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (context is SwitcherInterface) {
            mController = context as SwitcherInterface
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement SwitcherInterface"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ReceiveFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ReceiveViewModel::class.java]
        viewModel.getSum()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sum.collect {
                    val amount = it ?: 0
                    binding.textViewBalanceRequire.text =
                        String.format(getString(R.string.your_balance_rubles), amount)
                }
            }
        }

        binding.buttonBill.setOnClickListener {
            val amountString = binding.editTextNeededSum.text.toString()
            if (amountString.isNotEmpty()) {
                val amount = amountString.toInt()
                if (amount > 0) viewModel.requireBanknotes(amount)
            } else makeToast(getString(R.string.enter_sum))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchingStatusFlow.collect {
                    when (it) {
                        SearchingStatus.NONE -> {
                            if (askForPermissions()) {
                                viewModel.startDiscovery()
                            }
                        }
                        SearchingStatus.FAILURE -> binding.requireStatusTextView.text =
                            getString(R.string.searching_failure)
                        SearchingStatus.DISCOVERING -> binding.requireStatusTextView.text =
                            getString(
                                R.string.searching_device
                            )
                        SearchingStatus.ADVERTISING -> binding.requireStatusTextView.text =
                            getString(
                                R.string.searching_device
                            )
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.requiringStatusFlow.collect {
                    when (it) {
                        RequiringStatus.NONE -> {
                            makeVisible(true)
                        }
                        RequiringStatus.REQUEST -> {
                            binding.requireStatusTextView.text =
                                getString(R.string.requiring_request)
                        }
                        RequiringStatus.REJECT -> {
                            binding.requireStatusTextView.text =
                                getString(R.string.requiring_reject)
                        }
                        RequiringStatus.ACCEPTANCE -> {
                            makeVisible(false)
                            binding.requireStatusTextView.text =
                                getString(R.string.requiring_acceptance)
                        }
                        RequiringStatus.COMPLETED -> {
                            makeToast(getString(R.string.requiring_completed))
                            mController.openWalletFragment()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.connectionResult.collect {
                    when (it) {
                        is ConnectingStatus.ConnectionInitiated -> {
                            binding.requireStatusTextView.text =
                                getString(R.string.connecting_initiated)
                            showConnectionDialog(it.info)
                        }
                        is ConnectingStatus.ConnectionResult ->
                            when (it.result.status.statusCode) {
                                ConnectionsStatusCodes.STATUS_OK -> {
                                    binding.buttonBill.isEnabled = true
                                    binding.requireStatusTextView.text =
                                        getString(R.string.connecting_ok)
                                }
                                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                                    binding.requireStatusTextView.text =
                                        getString(R.string.connecting_rejected)
                                }
                                ConnectionsStatusCodes.STATUS_ERROR -> {
                                    binding.requireStatusTextView.text =
                                        getString(R.string.connecting_error)
                                }
                                else -> {
                                    binding.requireStatusTextView.text =
                                        getString(R.string.connecting_undefined_error)
                                }
                            }
                        ConnectingStatus.Disconnected -> {
                            makeVisible(true)
                            binding.requireStatusTextView.text =
                                getString(R.string.connecting_disconnected)
                            binding.buttonBill.isEnabled = false
                        }
                        ConnectingStatus.NoConnection -> {
                            binding.buttonBill.isEnabled = false
                        }
                    }

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopDiscovery()
    }

    private fun makeVisible(visible: Boolean) {
        if (visible) {
            binding.billTitle.visibility = View.VISIBLE
            binding.editTextNeededSum.visibility = View.VISIBLE
            binding.buttonBill.visibility = View.VISIBLE
            binding.progressBarRequire.visibility = View.INVISIBLE
        } else {
            binding.billTitle.visibility = View.INVISIBLE
            binding.editTextNeededSum.visibility = View.INVISIBLE
            binding.buttonBill.visibility = View.INVISIBLE
            binding.progressBarRequire.visibility = View.VISIBLE
        }
    }

    private fun showConnectionDialog(info: ConnectionInfo) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.accept_connection_to) + info.endpointName)
            .setMessage(getString(R.string.make_sure) + info.authenticationDigits)
            .setPositiveButton(
                getString(R.string.receive)
            ) { _, _ ->  // The user confirmed, so we can accept the connection.
                viewModel.acceptConnection()
            }
            .setNegativeButton(
                getString(R.string.cancel)
            ) { _, _ ->  // The user canceled, so we should reject the connection.
                viewModel.rejectConnection()
            }
            //           .setIcon(ContextCompat.getDrawable(view.context, R.drawable.ic_dialog_alert))
            .show()
            .setCanceledOnTouchOutside(false)
    }

    //Runtime permissions

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.location_denied))
            .setMessage(getString(R.string.allow_perm_in_settings))
            .setPositiveButton(getString(R.string.settings)) { _, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", requireActivity().application.packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
            .setCanceledOnTouchOutside(false)
    }

    private val activityResultLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.values.all { it == true }) {
                viewModel.startDiscovery()
            } else {
                binding.requireStatusTextView.text = getString(R.string.permission_rejected)
                showPermissionDeniedDialog()
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
            if (shouldShowRequestPermissionRationale(permission)) {
                showPermissionDeniedDialog()
            } else {
                activityResultLauncher.launch(arrayOf(permission))
            }
            return false
        }
        return true
    }
}