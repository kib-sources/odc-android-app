package npo.kib.odc_demo.feature_app.presentation.p2p_screens.send_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import npo.kib.odc_demo.R
import npo.kib.odc_demo.common.util.collectFlow
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.AmountRequest
import npo.kib.odc_demo.feature_app.domain.model.connection_status.ConnectingStatus
import npo.kib.odc_demo.feature_app.domain.model.connection_status.SearchingStatus
import npo.kib.odc_demo.databinding.SendFragmentBinding
import npo.kib.odc_demo.common.util.makeToast
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.nearby_screen.BaseNearbyFragment

class SendFragment : BaseNearbyFragment() {

    companion object {
        fun newInstance() = SendFragment()
    }

    private var _binding: SendFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override lateinit var viewModel: SendViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = SendFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[SendViewModel::class.java]
        viewModel.updateCurrentSum()
        var currentAmount = 0

        viewLifecycleOwner.collectFlow(viewModel.amountRequestFlow) { request: AmountRequest? ->
            if (request != null) {
                showRequestDialog(request.amount, request.userName, request.wid)
            }
        }


        viewLifecycleOwner.collectFlow(viewModel.sum) {
            currentAmount = it ?: 0
            binding.textViewBalanceSend.text =
                String.format(getString(R.string.your_balance_rubles), currentAmount)
        }

        binding.buttonSend.setOnClickListener {
            val amountString = binding.editTextSend.text.toString()
            if (amountString.isNotEmpty()) {
                val amountToSend = amountString.toInt()
                if (amountToSend > 0) {
                    if (amountToSend <= currentAmount) {
                        viewModel.sendBanknotes(amountToSend)
                    }
                    else makeToast(getString(R.string.insufficient_funds))
                }
            }
            else makeToast(getString(R.string.enter_sum))
        }

        viewLifecycleOwner.collectFlow(viewModel.searchingStatusFlow) {
            when (it) {
                SearchingStatus.NONE -> viewModel.startAdvertising()
                SearchingStatus.FAILURE -> binding.sendingStatusView.text =
                    getString(R.string.searching_failure)

                SearchingStatus.DISCOVERING -> binding.sendingStatusView.text =
                    getString(R.string.searching_device)

                SearchingStatus.ADVERTISING -> binding.sendingStatusView.text =
                    getString(R.string.searching_device)
            }
        }

        viewLifecycleOwner.collectFlow(viewModel.isSendingFlow) { isSending: Boolean? ->
            isSending ?: return@collectFlow

            if (isSending) {
                makeVisible(false)
                binding.sendingStatusView.text = getString(R.string.sending_is_going)
            }
            else {
                makeToast(getString(R.string.sending_completed))
                mController.openHomeFragment()
            }
        }

        viewLifecycleOwner.collectFlow(viewModel.connectionResult) {
            when (it) {
                is ConnectingStatus.ConnectionInitiated -> {
                    binding.sendingStatusView.text =
                        getString(R.string.connecting_initiated)
                    showConnectionDialog(it.info)
                }

                is ConnectingStatus.ConnectionResult ->
                    when (it.statusCode) {
                        ConnectionsStatusCodes.STATUS_OK -> {
                            binding.sendingStatusView.text =
                                getString(R.string.connecting_ok)
                            binding.buttonSend.isEnabled = true
                        }

                        ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                            binding.sendingStatusView.text =
                                getString(R.string.connecting_rejected)
                        }

                        ConnectionsStatusCodes.STATUS_ERROR -> {
                            binding.sendingStatusView.text =
                                getString(R.string.connecting_error)
                        }

                        else -> {
                            binding.sendingStatusView.text =
                                getString(R.string.connecting_undefined_error)
                        }
                    }

                ConnectingStatus.Disconnected -> {
                    viewModel.updateCurrentSum()
                    makeVisible(true)
                    binding.sendingStatusView.text =
                        getString(R.string.connecting_disconnected)
                    binding.buttonSend.isEnabled = false
                }

                ConnectingStatus.NoConnection -> {
                    binding.buttonSend.isEnabled = false
                    binding.sendingStatusView.text = getString(R.string.searching_device)
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
        viewModel.stopAdvertising()
    }

    private fun makeVisible(visible: Boolean) {
        if (visible) {
            binding.sendTitle.visibility = View.VISIBLE
            binding.editTextSend.visibility = View.VISIBLE
            binding.buttonSend.visibility = View.VISIBLE
            binding.progressBarSend.visibility = View.INVISIBLE
        }
        else {
            binding.sendTitle.visibility = View.INVISIBLE
            binding.editTextSend.visibility = View.INVISIBLE
            binding.buttonSend.visibility = View.INVISIBLE
            binding.progressBarSend.visibility = View.VISIBLE
        }
    }

    private fun showRequestDialog(amount: Int, userName: String, wid: String) {
        val dialogView = layoutInflater.inflate(R.layout.request_dialog, null)
        dialogView.setBackgroundResource(R.drawable.border_radius)

        val descriptionView = dialogView.findViewById<TextView>(R.id.dialogDescription)
        val amountView = dialogView.findViewById<TextView>(R.id.dialogAmount)
        val sendButton = dialogView.findViewById<Button>(R.id.dialogButtonSend)
        val denyButton = dialogView.findViewById<Button>(R.id.dialogButtonDeny)

        descriptionView.text =
            String.format(getString(R.string.request_banknotes_description), userName, wid)
        amountView.text = String.format(getString(R.string.balance_rubles), amount)

        val alert = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        sendButton.setOnClickListener {
            viewModel.sendBanknotes(amount)
            alert.cancel()
        }
        denyButton.setOnClickListener {
            viewModel.sendRejection()
            alert.cancel()
        }
        alert.show()
    }

    //Runtime permissions
    override fun onPermissionGranted() {
        viewModel.startAdvertising()
    }

    override fun onPermissionRejected() {
        binding.sendingStatusView.text = getString(R.string.permission_rejected)
    }
}