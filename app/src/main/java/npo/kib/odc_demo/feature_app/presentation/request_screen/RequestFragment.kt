package npo.kib.odc_demo.feature_app.presentation.request_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import npo.kib.odc_demo.R
import npo.kib.odc_demo.common.util.collectFlow
import npo.kib.odc_demo.feature_app.domain.model.types.ConnectingStatus
import npo.kib.odc_demo.feature_app.domain.model.types.RequiringStatus
import npo.kib.odc_demo.feature_app.domain.model.types.SearchingStatus
import npo.kib.odc_demo.databinding.ReceiveFragmentBinding
import npo.kib.odc_demo.common.util.makeToast
import npo.kib.odc_demo.feature_app.presentation.nearby_screen.BaseNearbyFragment

class RequestFragment : BaseNearbyFragment() {

    companion object {
        fun newInstance() = RequestFragment()
    }

    private var _binding: ReceiveFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override lateinit var viewModel: RequestViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = ReceiveFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[RequestViewModel::class.java]
        viewModel.getCurrentSum()

        viewLifecycleOwner.collectFlow(viewModel.sum) {
            val amount = it ?: 0
            binding.textViewBalanceRequire.text = String.format(getString(R.string.your_balance_rubles), amount)
        }

        binding.buttonBill.setOnClickListener {
            val amount = binding.editTextNeededSum.text.toString().toIntOrNull()
            if (amount == null) {
                makeToast(getString(R.string.enter_sum))
                return@setOnClickListener
            }

            if (amount > 0) {
                viewModel.requireBanknotes(amount)
            }
        }

        viewLifecycleOwner.collectFlow(viewModel.searchingStatusFlow) {
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

        viewLifecycleOwner.collectFlow(viewModel.requiringStatusFlow) {
            when (it) {
                RequiringStatus.NONE -> {
                    makeVisible(true)
                }
                RequiringStatus.REQUEST -> {
                    binding.requireStatusTextView.text = getString(R.string.requiring_request)
                }
                RequiringStatus.REJECT -> {
                    binding.requireStatusTextView.text = getString(R.string.requiring_reject)
                }
                RequiringStatus.ACCEPTANCE -> {
                    makeVisible(false)
                    binding.requireStatusTextView.text = getString(R.string.requiring_acceptance)
                }
                RequiringStatus.COMPLETED -> {
                    makeToast(getString(R.string.requiring_completed))
                    mController.openHomeFragment()
                }
            }
        }

        viewLifecycleOwner.collectFlow(viewModel.connectionResult) {
            when (it) {
                is ConnectingStatus.ConnectionInitiated -> {
                    binding.requireStatusTextView.text = getString(R.string.connecting_initiated)
                    showConnectionDialog(it.info)
                }
                is ConnectingStatus.ConnectionResult ->
                    when (it.statusCode) {
                        ConnectionsStatusCodes.STATUS_OK -> {
                            binding.buttonBill.isEnabled = true
                            binding.requireStatusTextView.text = getString(R.string.connecting_ok)
                        }
                        ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                            binding.requireStatusTextView.text = getString(R.string.connecting_rejected)
                        }
                        ConnectionsStatusCodes.STATUS_ERROR -> {
                            binding.requireStatusTextView.text = getString(R.string.connecting_error)
                        }
                        else -> {
                            binding.requireStatusTextView.text = getString(R.string.connecting_undefined_error)
                        }
                    }
                ConnectingStatus.Disconnected -> {
                    viewModel.getCurrentSum()
                    makeVisible(true)
                    binding.requireStatusTextView.text = getString(R.string.connecting_disconnected)
                    binding.buttonBill.isEnabled = false
                }
                ConnectingStatus.NoConnection -> {
                    binding.buttonBill.isEnabled = false
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

    //Runtime permissions
    override fun onPermissionGranted() {
        viewModel.startDiscovery()
    }

    override fun onPermissionRejected() {
        binding.requireStatusTextView.text = getString(R.string.permission_rejected)
    }
}